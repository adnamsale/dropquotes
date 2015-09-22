function solveFunc()  {
    var button = document.getElementById('solveButton');
    var msg = document.getElementById('solveMessage');
    if (button.value !== 'Solve') {
        return;
    }
    button.value = 'Solving...';
    msg.innerHTML = '';
    var src = document.body.innerHTML;

    // We need to dispatch to the global page to access our preferences
    safari.self.tab.dispatchMessage("solve", src);
}

function getAnswer(theMessageEvent) {
    if (theMessageEvent.name === 'theAnswer') {
        var button = document.getElementById('solveButton');
        if (!button) {
            return;
        }
        var msg = document.getElementById('solveMessage');

        var status = theMessageEvent.message[0];
        var responseText = theMessageEvent.message[1];

        button.value = 'Solve';

        if (status === 200) {
            var resp = JSON.parse(responseText);
            if (resp.answer) {
                var letters = resp.answer.replace(/ /g, '');
                for (var i = 0 ; i < letters.length ; ++i) {
                    var id = "box" + (i + 1);
                    var elem = document.getElementById(id);
                    elem.value = letters.charAt(i);
                }
            }
            else if (resp.error) {
                msg.innerHTML = 'Error from server: ' + resp.error;
            }
            else {
                msg.innerHTML = 'No solution found';
            }
        }
        else {
            msg.innerHTML = 'Error: Server returned status ' + status;
        }
    }
}

var container = document.getElementById('formResponse');
if (container) {
    var newButton = document.createElement('input');
    newButton.type = 'button';
    newButton.value = 'Solve';
    newButton.id = 'solveButton';
    newButton.onclick = solveFunc;
    container.appendChild(newButton);
    var msg = document.createElement('span');
    msg.id = 'solveMessage';
    container.appendChild(msg);
}

safari.self.addEventListener("message", getAnswer, false);
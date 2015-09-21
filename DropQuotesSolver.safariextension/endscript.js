var solveFunc = function() {
    var button = document.getElementById('solveButton');
    var msg = document.getElementById('solveMessage');
    if (button.value !== 'Solve') {
        return;
    }
    button.value = 'Solving...';
    msg.innerHTML = '';
    var src = document.body.innerHTML;
    var postdata = JSON.stringify({'src' : src});
    var req = new XMLHttpRequest();
//    req.open('POST', 'http://localhost:8080/dropquotes/solve');
    req.open('POST', 'https://rtqxwn6ek5.execute-api.us-east-1.amazonaws.com/prod/dropquotes/solve');
    req.setRequestHeader('Content-Type', 'application/json');
    req.onload = function() {
        if (req.status === 200) {
            var resp = JSON.parse(req.responseText);
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
        }
        else {
            msg.innerHTML = 'Error: Server returned status ' + req.status;
        }
        button.value = 'Solve';
    };
    req.onerror = function() {
        msg.innerHTML = 'Error: Unable to connect to server';
        button.value = 'Solve';
    }
    req.send(postdata);
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
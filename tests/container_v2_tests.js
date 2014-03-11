//6. Test case for quit game
function testQuitMatch(successCallback) {
    $.ajax({
        url: "http://2.smg-server.appspot.com/matches/10001/quitgame",
        dataType: "json",
        type: "POST",
        data: {
            "accessSignature": "hD7d7DDdjsh12WQ",
            "matchId": 10001,
            "result": {
                "normal_end": false,
                "win": 42,
                "lose": 43,
                "token_game": false,
                "win_score": 30,
                "lose_score": 10,
                "penalty": 50
            }
        },
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            alert("error " + textStatus + " " + errorThrown);
        }
    });
}

test("Quit Match", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);     
    testQuitMatch(function(actual) {
        var expected = { 
          "matchId": 10001
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
        start();
    })
})

//Test case for quit game - illegal AS
function testQuitMatchIllegalAS(successCallback) {
    $.ajax({
        url: "http://2.smg-server.appspot.com/matches/10001/quitgame",
        dataType: "json",
        type: "POST",
        data: {
            "accessSignature": "hD7d7DDdjsh12WQ",
            "matchId": 10001,
            "result": {
                "normal_end": false,
                "win": 42,
                "lose": 43,
                "token_game": false,
                "win_score": 30,
                "lose_score": 10,
                "penalty": 50
            }
        },
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            alert("error " + textStatus + " " + errorThrown);
        }
    });
}

test("Quit Match with illegal access signature", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);     
    testQuitMatchIllegalAS(function(actual) {
        var expected = { 
          "error": "accessSignature illegal"
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
        start();
    })
})

//Test case for quit game - illegal Match Id
function testQuitMatchIllegalMatchId(successCallback) {
    $.ajax({
        url: "http://2.smg-server.appspot.com/matches/10001/quitgame",
        dataType: "json",
        type: "POST",
        data: {
            "accessSignature": "hD7d7DDdjsh12WQ",
            "matchId": 10001,
            "result": {
                "normal_end": false,
                "win": 42,
                "lose": 43,
                "token_game": false,
                "win_score": 30,
                "lose_score": 10,
                "penalty": 50
            }
        },
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            alert("error " + textStatus + " " + errorThrown);
        }
    });
}

test("Quit Match with illegal match Id", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);     
    testQuitMatchIllegalMatchId(function(actual) {
        var expected = { 
          "error": "match Id illegal"
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
        start();
    })
})

// Case for how much time left.
function testHowMuchTimeLeft(successCallback){
    $.ajax({
        url: 'http://2.smg-server.appspot.com/get_time_remaining/42',
        dataType: 'json',
        type: 'GET',
        data: {
            'accessSignature': "hD7d7DDdjsh12WQ",
            "type": "get_time_remaining"
        },
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            alert('error ' + textStatus + " " + errorThrown);
        }
    });
}

test("How Much Time Left", function(){
    stop(1000);     
    testHowMuchTimeLeft(function(actual) {
        var expected = { 
          'time_remaining': 1
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
        start();
    })
})

// Case for the ability to move forward as well as back.
function testMoveBack(successCallback){ 
    $.ajax({
        url: 'http: //1.smg-server.appspot.com/state_history',
        dataType: 'json',
        type: 'GET',
        data: {
            'accessSignature': ACCESS_SIGNATURE,
            'type': 'get_back_state'
        },
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            alert('error ' + textStatus + " " + errorThrown);
        }
    });
}

test("Move Back", function(){
    stop(1000);     
    testMoveBack(function(actual) {
        var expected = {
                'gameState': {
                    'state_1':'state_1',
                    'state_2': 'state_2'
                }
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
        start();
    })
})

// Case for the ability to move forward as well as back.
function testMoveForward(successCallback){ 
    $.ajax({
        url: 'http: //2.smg-server.appspot.com/state_history',
        dataType: 'json',
        type: 'GET',
        data: {
            'accessSignature': ACCESS_SIGNATURE,
            'type': 'get_forward_state'
        },
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            alert('error ' + textStatus + " " + errorThrown);
        }
    });
}

test("Move Forward", function(){
    stop(1000);     
    testMoveForward(function(actual) {
        var expected = {
                'gameState': {
                    'state_1':'state_1',
                    'state_2': 'state_2'
                }
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
        start();
    })
})

// Bet on tokens AND ALSO change current bet.
function testChangeBet(successCallback){
    $.ajax({
        url: 'http: //2.smg-server.appspot.com/bet',
        dataType: 'json',
        type: 'POST',
        data: {
            'accessSignature': ACCESS_SIGNATURE,
            'type': 'set_bet_token_number',
            'token': 9
        },
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            alert('error ' + textStatus + " " + errorThrown);
        }
    });
}

test("Change Bet", function(){
    stop(1000);     
    testMoveForward(function(actual) {
        var expected = {
            'token': 9
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
        start();
    })
})

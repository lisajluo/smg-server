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



PLAYER_ID = 1042
ACCESS_SIGNATURE = "hD7d7DDdjsh12WQ"

// Case for how much time left.
$.ajax({
    url: 'http://1.smg-server.appspot.com/get_time_remaining/' + PLAYER_ID,
    dataType: 'json',
    type: 'GET',
    data: {
        'accessSignature': ACCESS_SIGNATURE,
        "type": "get_time_remaining"
    },
    success: function (actural) {
        var expectedData = {
            'time_remaining': 1
        }
        assert.deepEqual(actural, expected);
    },
    error: function (jqXHR, textStatus, errorThrown) {
        alert('error ' + textStatus + " " + errorThrown);
    }
});

// Case for the ability to move forward as well as back.
$.ajax({
    url: 'http: //1.smg-server.appspot.com/state_history',
    dataType: 'json',
    type: 'GET',
    data: {
        'accessSignature': ACCESS_SIGNATURE,
        'type': 'get_back_state'
    },
    success: function (actural) {
        var expectedData = {
            'gameState': {...
            }
        }
        assert.deepEqual(actural, expected);
    },
    error: function (jqXHR, textStatus, errorThrown) {
        alert('error ' + textStatus + " " + errorThrown);
    }
});

$.ajax({
    url: 'http: //1.smg-server.appspot.com/state_history',
    dataType: 'json',
    type: 'GET',
    data: {
        'accessSignature': ACCESS_SIGNATURE,
        'type': 'get_forward_state'
    },
    success: function (actural) {
        var expectedData = {
            'gameState': {...
            }
        }
        assert.deepEqual(actural, expected);
    },
    error: function (jqXHR, textStatus, errorThrown) {
        alert('error ' + textStatus + " " + errorThrown);
    }
});

// Bet on tokens AND ALSO change current bet.
$.ajax({
    url: 'http: //1.smg-server.appspot.com/bet',
    dataType: 'json',
    type: 'POST',
    data: {
        'accessSignature': ACCESS_SIGNATURE,
        'type': 'set_bet_token_number',
        'token': 9
    },
    success: function (actural) {
        var expectedData = {}
        assert.deepEqual(actural, expected);
    },
    error: function (jqXHR, textStatus, errorThrown) {
        alert('error ' + textStatus + " " + errorThrown);
    }
});

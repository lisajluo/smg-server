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
//1. Test case for Make a Move - Send
function testMakeMove (successCallback){
    $.ajax({
        url: "http://1.smg-server.appspot.com/matches/10001",
        dataType: "json",
        type: "POST",
        data: {
            "accessSignature": "hD7d7DDdjsh12WQ", 
            "operations": {
                "op1": "op1_detail",
                "op2": "op2_detail",
                "flag": "some_flag"
            }
        },
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            alert("error " + textStatus + " " + errorThrown);
        }
    });
}

test ("Make Move", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);

    testMakeMove(function(actural) {
        var expected = {
            "accessSignature": "hD7d7DDdjsh12WQ", 
            "gameState": { 
                "s_1":"s_1",
                "s_2":"s_2",
                "s_3":"s_3",
                "flag":"some_flag"
            }
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
        
        start();
    })
})

//2. Test case for Receive game state
function testGetGameState (successCallback){
    $.ajax({
        url: "http://1.smg-server.appspot.com/matches/10001?accessSignature=hD7d7DDdjsh12WQ",
        dataType: "json",
        type: "GET",
        data: {},
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            alert("error " + textStatus + " " + errorThrown);
        }
    });
}

test ("Get Game State", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);     
    testGetGameState(function(actural) {
        var expected = {
            "accessSignature": "hD7d7DDdjsh12WQ", 
            "gameState": { 
                "s_1":"s_1",
                "s_2":"s_2",
                "s_3":"s_3",
                "flag":"some_flag"
            }
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
        start();
    })
})

//3. Test case for get user profile
function testGetUserProfile (successCallback){
    $.ajax({
        url: "http://1.smg-server.appspot.com/users/43?accessSignature=hD7d7DDdjsh12WQ",
        dataType: "json",
        type: "GET",
        data: {},
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            alert("error " + textStatus + " " + errorThrown);
        }
    });
}

test ("Get User Profile", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);     
    testGetUserProfile(function(actural) {
        var expected = {
            "player_id": 43,
            "nickname": "foo",
            "image": "http://www.foo.com/bar.gif",
            "rank": {
                1001:1,
                1002:200,
                1003:20
            },
            "tokens": 2000
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
        start();
    })
})

//4. Test case for insert a match
function testInsertAMatch (successCallback){
    $.ajax({
        url: "http://1.smg-server.appspot.com/matches/",
        dataType: "json",
        type: "POST",
        data: {
            "accessSignature": "hD7d7DDdjsh12WQ",
            "playerIds": [42, 43],
            "gameId": 1001
        },
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            alert("error " + textStatus + " " + errorThrown);
        }
    });
}

test ("Insert a Match", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);     
    testInsertAMatch(function(actual) {
        var expected = { 
          "matchId": 10001
        };
        // How to know the 10001? 
        deepEqual(expected, actual, "Expected data does not match actual data.");
        start();
    })
})

//5. Test case for normal end game
function testNormalEndMatch (successCallback){
    $.ajax({
        url: "http://1.smg-server.appspot.com/matches/10001/endgame",
        dataType: "json",
        type: "POST",
        data: {
            "accessSignature": "hD7d7DDdjsh12WQ",
            "matchId": 10001,
            "result": {
                "normal_end": true,
                "win": 42,
                "lose": 43,
                "token_game": true,
                "win_token": 100,
                "lose_token": 50,
                "win_score": 30,
                "lose_score": 10
            }
        },
        success: successCallback,
        error: function (jqXHR, textStatus, errorThrown) {
            alert("error " + textStatus + " " + errorThrown);
        }
    });
}

test ("Normal End Match", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);     
    testNormalEndMatch(function(actual) {
        var expected = { 
          "matchId": 10001
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
        start();
    })
})
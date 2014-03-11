// Test for getting game info 
function testGetGameInfoSuccess(successCallback) {
  $.ajax({
    url: "http://2.smg-server.appspot.com/games/" + gameId + "?accessSignature=" + 
       accessSignature,
    dataType: "json",
    type: "GET",
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Get game info success", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    testGetGameInfoSuccess(function(actual) {
        var expected = {
          "playerIdOfGameDeveloper": 1,
          "url": "http://www.foobar.com/",
          "name": "The World's Most Amazing Game",
          "description": "This game is actually quite self-explanatory",
          "parameters": {
            "width": 500,
            "height": 600,
            "minPlayers": 2,
            "maxPlayers": 2,
            "passAndPlay": false,
            "perfectInformation": true,
            "timePerTurn": 20
          },
          "pics": [
            "http://www.foo.com/bar1.gif", 
            "http://www.foo.com/bar2.gif", 
            "http://www.foo.com/bar3.gif"
          ],
          "statistics": {
            "networkUsage": [
              {
                "date": Date(298394834),
                "usage": "50MB"
              },
              {
                "date": Date(541654545),
                "usage": "2MB"
              }
            ],
            "responseTime": [
              {
                "date": Date(298394834),
                "time": "20ms"
              },
              {
                "date": Date(541654545),
                "time": "30ms"
              }
            ],
            "aggregates": {
              "averageMoveTime": "32s",
              "averageMoveCount": 83.9393
            }
          },
          
          "hackers": [
            {
              "hackerId": 532,
              "hackerDate": Date(123982948),
              "expectedMove":  "..",
              "actualMove":  ".." 
            },
            {
              "hackerId": 445,
              "hackerDate": Date(6546546545),
              "expectedMove":  "..",
              "actualMove":  ".." 
            }
          ],
          "bugs": [
             {
              "bugType": 0,
              "bugTrace": "Calling function f(), Null pointer exception"
             }
          ]
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for getting game info with incorrect accessSignature
function testGetGameInfoWrongAccessSignature(successCallback) {
  $.ajax({
    url: "http://2.smg-server.appspot.com/games/" + gameId + "?accessSignature=" + 
        wrongAccessSignature,
    dataType: "json",
    type: "GET",
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Get game info wrong access signature", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    testGetGameInfoWrongAccessSignature(function(actual) {
        var expected = {
          "error": "WRONG_ACCESS_SIGNATURE" 
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for getting game info for a developer with incorrect gameId
function testGetGameInfoWrongGameId(successCallback) {
  $.ajax({
    url: "http://2.smg-server.appspot.com/games/" + wrongGameId+ "?accessSignature=" + 
       accessSignature,
    dataType: "json",
    type: "GET",
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Get game info wrong game Id", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    testGetGameInfoWrongGameId(function(actual) {
        var expected = {
          "error": "WRONG_GAME_ID" 
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


  
// Test for updating game FAQ to an existing (correct) gameId
function testupdatingFAQ(successCallback) {
	$.ajax({
	  url: "http://2.smg-server.appspot.com/games/" + gameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "FAQ": [{"question":"How should I start this Game?", "answer": "Simply click start"}]
	  },
	  success: successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}

test("Test updating FAQ", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);     
    testupdatingFAQ(function(actual) {
        var expected = { 
         "success": "UPDATED_GAME" 
         };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})
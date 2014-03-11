// Test for updating player numbers to an existing (correct) gameId
function testUpdateGamePlayerNumbersSuccess(successCallback) {
  $.ajax({
    url: "http://3.smg-server.appspot.com/games/" + gameId,
    dataType: "json",
    type: "PUT",
    data: {
      "accessSignature": accessSignature,
      "parameters": {
        "minPlayers": 2,
        "maxPlayers": 4,
      }
    },
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Update game player numbers success", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    testUpdateGamePlayerNumbersSuccess(function(actual) {
        var expected = {
          "success": "UPDATED_GAME" 
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for updating player numbers to an an incorrect gameId
function updatingPlayerNumberToIncorrectId(successCallback) {
	$.ajax({
	  url: "http://3.smg-server.appspot.com/games/" + wrongGameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "parameters": {
	      "minPlayers": 2,
	      "maxPlayers": 4,
	    }
	  },
	  success: successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}



test("Update player number to incorrect Id", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    updatingPlayerNumberToIncorrectId(function(actual) {
        var  expected = { 
	      "error": "WRONG_GAME_ID" 
	    };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})

// Test for updating player numbers to an an incorrect accessSignature
function updatingPlayerNumbersWithIncorrctSignature(successCallback) {
	$.ajax({
	  url: "http://3.smg-server.appspot.com/games/" + gameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": wrongAccessSignature,
	    "parameters": {
	      "minPlayers": 2,
	      "maxPlayers": 4,
	    }
	  },
	  success: successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}

test("Update player number to With incorrect signature", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
   updatingPlayerNumbersWithIncorrctSignature(function(actual) {
        var expected = { 
	      "error": "WRONG_ACCESS_SIGNATURE" 
	    };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for updating whether game has AI
function updatingAI(successCallback) {
	$.ajax({
	  url: "http://3.smg-server.appspot.com/games/" + gameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "parameters": {
	      "enableAI": true
	    }
	  },
	  success: successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}

test("Update AI option", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
   updatingAI(function(actual) {
       var expected = { 
	      "success": "UPDATED_GAME" 
	    };

        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for updating whether game has AI to an an incorrect gameId
function updatingAIwithIncorrctId(successCallback) {
	$.ajax({
	  url: "http://3.smg-server.appspot.com/games/" + wrongGameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "parameters": {
	      "enableAI": true
	    }
	  },
	  success: successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}

test("Update AI option with incorrect Id", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
   updatingAIwithIncorrctId(function(actual) {
        var expected = { 
	      "error": "WRONG_GAME_ID" 
	    };


        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})
// Test for updating whether game has AI to an an incorrect accessSignature
function updatingAIwithIncorrctSignature(successCallback) {
	$.ajax({
	  url: "http://3.smg-server.appspot.com/games/" + gameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": wrongAccessSignature,
	    "parameters": {
	      "enableAI": true
	    }
	  },
	  success: successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}

test("Update AI option with incorrect signature", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
   updatingAIwithIncorrctSignature(function(actual) {
        var expected = { 
	     "error": "WRONG_ACCESS_SIGNATURE" 
	    };

        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})

// Test for updating whether game is turn-based
function updatingTurnBased(successCallback) {
	$.ajax({
	  url: "http://3.smg-server.appspot.com/games/" + gameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "parameters": {
	      "turnBased": false
	    }
	  },
	  success: successCallback,
	  
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}

test("Update Turn Based", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
   updatingTurnBased(function(actual) {
        var expected = { 
	      "success": "UPDATED_GAME" 
	    };

        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})

// Test for updating whether game is turn-based to an an incorrect gameId
function updatingTurnBasedWithIncorrctId(successCallback) {
	$.ajax({
	  url: "http://3.smg-server.appspot.com/games/" + wrongGameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "parameters": {
	      "turnBased": false
	    }
	  },
	  success: successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}

test("Update Turn Based with incorrct Id", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    updatingTurnBasedWithIncorrctId(function(actual) {
        var expected = { 
	      "error": "WRONG_GAME_ID" 
	    };

        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})

// Test for updating whether game is turn-based to an an incorrect accessSignature
function updatingTurnBasedWithIncorrctSignature(successCallback) {
	$.ajax({
	  url: "http://3.smg-server.appspot.com/games/" + gameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": wrongAccessSignature,
	    "parameters": {
	      "turnBased": false
	    }
	  },
	  success: successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}


test("Update Turn Based with incorrct Signature", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    updatingTurnBasedWithIncorrctSignature(function(actual) {
        var expected = { 
	      "error": "WRONG_ACCESS_SIGNATURE"  
	    };

        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})

 // Test for viewing popularity comparison
function viewPopularityComparison(successCallback) {
	$.ajax({
	  url: "http://3.smg-server.appspot.com/games/" + gameId + "?accessSignature=" + 
	       wrongAccessSignature  ,
	  dataType: "json",
	  type: "GET",
	    success: successCallback,
	    error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}



test("View Popularity Comparison", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    viewPopularityComparison(function(actual) {
        var expected = { 
	       data: {
	       "popularity": 
	        {
	          1:100,  //1 stands for gameID, 100 stands for popularity
	          2:200,
	          3:200,
	          4:300,
	          5:300
	        }} 
	    };

        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})
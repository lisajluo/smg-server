// Test for creating a new game successfully
function testCreateGameSuccess(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/games/",
    dataType: "json",
    type: "POST",
    data: {
      "accessSignature": accessSignature,
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
      ]
    },
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Create Game Success", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
     
    testCreateGameSuccess(function(actual) {
        var expected = { 
          "gameId": 1234
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})

//create a new developer successfully
function testcreateNewDeveloper(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/players/",
    dataType: "json",
    type: "POST",
    data: {
      "E-mail":  "abcd@gmail.com",
      "password": "123456",
      "FirstName": "Lisa",
      "LastName": "Luo",
    },
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test ("Create Developer Success ", function() {
  // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
     
    testCreateNewDeveloper(function(actual) {
       var expected = { 
          "gameId": 1234,
          "accessSignature": accessSignature
        }; 
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})

//create a new developer unsuccessfully with registered email
function testcreateNewDeveloperWithUsedEmail(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/players/",
    dataType: "json",
    type: "POST",
    data: {
      "E-mail":  usedEmail,
      "password": "123456",
      "FirstName": "Lisa",
      "LastName": "Luo",
    },
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Create a new developer with registered email", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
     
    testCreateNewDeveloperWithUsedEmail(function(actual) {
        var expected = { 
          "error": "EMAIL_EXISTS"
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})

// Test for successful Login 
function testSuccessfulLogin(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/players/{playerId}?password=correctpw",
    dataType: "json",
    type: "GET",
    data: {},
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Test for successful login", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
     
    testSuccessfulLogin(function(actual) {
         var expected = { 
          "gameId": developerId,
          "accessSignature": accessSignature
        }; 

        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})

// Test for unsuccessful Login with incorrect password 
function testUnSuccessfulLoginWithIncorrectpw(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/players/{playerId}?password=incorrectpw",
    dataType: "json",
    type: "GET",
    data: {},
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Test for unsuccessful login with incorrect password", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
     
    testUnSuccessfulLoginWithIncorrctpw(function(actual) {
         var expected = { 
          "error": "WRONG_PASSWORD"
        }; 

        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for successful Delete 
function testSuccessfulDelete(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/players/{playerId}?accessSignature=correct",
    dataType: "json",
    type: "DELETE",
    data: {},
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Test for successful Delete", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
     
    testSuccessfulDelete(function(actual) {
         var expected = { 
          "success": "DELETED_PLAYER"
        }; 

        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for unsuccessful Delete with wrong signature
function testUnsuccessfulDeleteWithWrongSignature(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/players/{playerId}?accessSignature=incorrect",
    dataType: "json",
    type: "DELETE",
    data: {},
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Test for unsuccessful Delete with wrong signature", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
     
   testUnsuccessfulDeleteWithWrongSignature(function(actual) {
         var expected = { 
          "ERROR": "WRONG_ACCESS_SIGNATURE"
        }; 

        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})



// Test for creating a new game unsuccessfully (wrong playerId)
function testCreateGameWrongPlayerId(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/games/",
    dataType: "json",
    type: "POST",
    data: {
      "accessSignature": accessSignature,
      "playerIdOfGameDeveloper": -1,
      "pics": [
        "http://www.foo.com/bar1.gif", 
        "http://www.foo.com/bar2.gif", 
        "http://www.foo.com/bar3.gif"
      ]
    },
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Create Game Wrong Player Id", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
     
    testCreateGameWrongPlayerId(function(actual) {
        var expected = { 
          "error": "WRONG_PLAYER_ID"
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for creating a new game unsuccessfully (wrong accessSignature)
function testCreateGameWrongAccessSignature(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/games/",
    dataType: "json",
    type: "POST",
    data: {
      "accessSignature": wrongAccessSignature,
      "playerIdOfGameDeveloper": 1231,
      "pics": [
        "http://www.foo.com/bar1.gif", 
        "http://www.foo.com/bar2.gif", 
        "http://www.foo.com/bar3.gif"
      ]
    },
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Create Game Wrong Access Signature", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
     
    testCreateGameWrongAccessSignature(function(actual) {
        var expected = { 
          "error": "WRONG_ACCESS_SIGNATURE"
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for authorizing a game successfully
function testAuthorizeGameSuccess(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/games/" + gameId,
    dataType: "json",
    type: "PUT",
    data: {
      "accessSignature": accessSignature,
      "isAuthorized": "true"
    },
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Authorize Game Successfully", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
     
    testAuthorizeGameSuccess(function(actual) {
        var expected = { 
          "success": "UPDATED_GAME" 
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for authorizing a game to a wrong gameId
function testAuthorizeGameWrongGameId(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/games/" + wrongGameId,
    dataType: "json",
    type: "PUT",
    data: {
      "accessSignature": accessSignature,
      "isAuthorized": "true"
    },
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Authorize Game Wrong Game Id", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
     
    testAuthorizeGameWrongGameId(function(actual) {
        var expected = { 
          "error": "WRONG_GAME_ID" 
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for authorizing a game to a wrong accessSignature
function testAuthorizeGameWrongAccessSignature(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/games/" + gameId,
    dataType: "json",
    type: "PUT",
    data: {
      "accessSignature": wrongAccessSignature,
      "isAuthorized": "true"
    },
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Authorize Game Wrong Access Signature", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
     
    testAuthorizeGameWrongAccessSignature(function(actual) {
        var expected = { 
          "error": "WRONG_ACCESS_SIGNATURE" 
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for updating width and height to an existing (correct) gameId
function testUpdatingWidthAndHeight(successCallback) {
	$.ajax({
	  url: "http://1.smg-server.appspot.com/games/" + gameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "parameters": { 
	      "width" :50 ,
	      "height" : 50
	    }
	  },
	  success: successCallback,
	  success: function(actual) {
	    var expected = { 
	      "success": "UPDATED_GAME" 
	    };
		  //assert.deepEqual(actual, expected); How should we deepEqual!?!?
	  },
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}

test("Update Width and Height", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
    testUpdatingWidthAndHeight(function(actual) {
       var expected = { 
      "success": "UPDATED_GAME" 
    };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})

// Test for updating url to an existing (correct) gameId
function testUpdatingUrl(successCallback) {
	$.ajax({
	  url: "http://1.smg-server.appspot.com/games/" + gameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "url" : "www.foo2.com"       
	  },
	  success: successCallback,
	  
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}


test("Update Url", function() {
    // Pause the test, and fail it if start() isn"t called after one second
    stop(1000);
    testUpdatingUrl(function(actual) {
       var expected = { 
      "success": "UPDATED_GAME" 
    };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})

// Test for updating screenshots to an existing (correct) gameId
function testUpdateScreenshotsSuccess(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/games/" + gameId,
    dataType: "json",
    type: "PUT",
    data: {
      "accessSignature": accessSignature,
      "pics": [
        "http://www.foo.com/bar1.gif", 
        "http://www.foo.com/bar2.gif", 
        "http://www.foo.com/bar3.gif"
      ]
    },
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Screenshots update success", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    testUpdateScreenshotsSuccess(function(actual) {
        var expected = { 
           "success": "UPDATED_GAME" 
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


function testUpdateScreenshotsWrongGameId(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/games/" + wrongGameId,
    dataType: "json",
    type: "PUT",
    data: {
      "accessSignature": accessSignature,
      "pics": [
        "http://www.foo.com/bar1.gif", 
        "http://www.foo.com/bar2.gif", 
        "http://www.foo.com/bar3.gif"
      ]
    },
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Screenshots update wrong Game Id", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    testUpdateScreenshotsWrongGameId(function(actual) {
        var expected = { 
           "error": "WRONG_GAME_ID" 
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for updating screenshots to a wrong accessSignature
function testUpdateScreenshotsWrongAccessSignature(successCallback) {
  $.ajax({
    url: "http://1.smg-server.appspot.com/games/" + wrongGameId,
    dataType: "json",
    type: "PUT",
    data: {
      "accessSignature": accessSignature,
      "pics": [
        "http://www.foo.com/bar1.gif", 
        "http://www.foo.com/bar2.gif", 
        "http://www.foo.com/bar3.gif"
      ]
    },
    success: successCallback,
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }
  });
}

test("Screenshots update wrong access signature", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    testUpdateScreenshotsWrongAccessSignature(function(actual) {
        var expected = { 
           "error": "WRONG_ACCESS_SIGNATURE" 
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for updating pass-and-play to an existing (correct) gameId
function testUpdatePassAndPlay(successCallback) {
	$.ajax({
	  url: "http://1.smg-server.appspot.com/games/" + gameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "parameters": {
	      "passAndPlay": false
	    }
	  },
	  success: successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}
test("UpdatePassAndPlay", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    testUpdatePassAndPlay(function(actual) {
        var expected = { 
           "success": "UPDATED_GAME"
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for updating perfect-info to an existing (correct) gameId
function testUpdatePerfectInfo(successCallback) {
	$.ajax({
	  url: "http://1.smg-server.appspot.com/games/" + gameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "parameters": {
	      "perfectInformation": false
	    }
	  },
	  success: successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}

test("UpdatePerfectInfo", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    testUpdatePerfectInfo(function(actual) {
        var expected = { 
           "success": "UPDATED_GAME"
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for updating pass-and-play to a nonexistent gameId
function testPassAndPlayWithNonexistId(successCallback) {
	$.ajax({
	  url: "http://1.smg-server.appspot.com/games/" + wrongGameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "parameters": {
	      "passAndPlay": false
	    },
	
	  },
	  success:successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}

test("UpdatePassAndPlayerWithNonexistId", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    testPassAndPlayWithNonexistId(function(actual) {
         var expected = { 
        "error": "WRONG_GAME_ID" 
         };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})

// Test for updating perfectInfo to a nonexistent gameId
function testPerfectInfoWithNonexistId(successCallback) {
	$.ajax({
	  url: "http://1.smg-server.appspot.com/games/" + wrongGameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "parameters": {
	      "perfectInformation": false
	    },
	
	  },
	  success: successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}
test("UpdatePerfectInfoWithNonexistId", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    testPerfectInfoWithNonexistId(function(actual) {
         var expected = { 
	        "error": "WRONG_GAME_ID" 
         };
        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})


// Test for updating description to an existing (correct) gameId
function testUpdatingDescription(successCallback) {
	$.ajax({
	  url: "http://1.smg-server.appspot.com/games/" + gameId,
	  dataType: "json",
	  type: "PUT",
	  data: {
	    "accessSignature": accessSignature,
	    "description": "Game info is being updated"
	  },
	  success: successCallback,
	  error: function(jqXHR, textStatus, errorThrown) {
	    alert("ERROR: " + textStatus + " " + errorThrown);
	  }
	});
}

test("UpdatingDescription", function() {
    // Pause the test, and fail it if start() isn't called after one second
    stop(1000);
     
    testUpdatingDescription(function(actual) {
           var expected = { 
            "success": "UPDATED_GAME" 
           };

        deepEqual(expected, actual, "Expected data does not match actual data.");
 
        start();
    })
})
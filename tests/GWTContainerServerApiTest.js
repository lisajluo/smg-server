/**
 * Created by Xiaocong, Yao, Tao 12/5/2014
 */

var serverName = "http://7-dot-smg-server.appspot.com/";
var gameId = "5059880838758400";

var playerId1;
var accessSignature1;
var playerId2;
var accessSignature2;

var matchId;

QUnit.config.reorder = false;

function produceRandomString(num) {
  var  x="0123456789qwertyuioplkjhgfdsazxcvbnm";
  var  tmp = "";
  for(var i=0;i<num;i++)   {
    tmp += x.charAt(Math.ceil(Math.random()*100000000)%x.length);
  }
  return tmp;
}

function registerTest(index) {
  var email = produceRandomString(16) + "@";
  email += produceRandomString(6) + ".com";
  var password = "123456789";
  var url = serverName + "user";
  var message = {
    "email": email,
    "password": password};
  $.ajax({
    url: url,
    dataType: 'json',
	data: JSON.stringify(message),
    type: "POST",
	success: function(data, textStatus, jqXHR) {
	  console.log(JSON.stringify(data));
	  if(data["error"]==undefined) {
		var playerId = data["userId"];
        var accessSignature = data["accessSignature"];
        if(index == 1) {
          playerId1 = playerId;
          accessSignature1 = accessSignature;
        }else if(index == 2) {
          playerId2 = playerId;
          accessSignature2 = accessSignature;
        }
        ok(true);
	  }else {
        ok(false);
	  }
	  start();
	},
	error: function(jqXHR, textStatus, errorThrown) {
	  console.log("Access Server Error: " + testStatus + " " + errorThrown);   
	}
  });
}

function enterQueueForMatchTest(index) {
  var accessSignature;
  var playerId;
  if(index == 1) {
    accessSignature = accessSignature1;
    playerId = playerId1;
  }else {
	accessSignature = accessSignature2;
	playerId = playerId2;
  }
  var message = {
    "accessSignature": accessSignature,
    "playerId": playerId,
    "gameId": gameId
  };
  var url = serverName + "queue";
  $.ajax({
    url: url,
    dataType: "json",
    type: "POST",
    data: JSON.stringify(message),
    success: function(data, textStatus, jqXHR) {
      console.log(JSON.stringify(data));
      if(data["error"] == undefined) {
        ok(true);
      }else {
        ok(false);
      }
      start();
    },
    error: function(jqXHR, textStatus, errorThrown) {
      console.log("Access Server Error: " + testStatus + " " + errorThrown);
    }
  });
}

function insertNewMatchTest() {
  var message = {
    "accessSignature": accessSignature1,
    "playerIds": [playerId1, playerId2],
	"gameId": gameId
  };
  var url = serverName + "newMatch/";
  $.ajax({
	url: url,
	dataType: "json",
	type: "POST",
	data: JSON.stringify(message),
	success: function(data, textStatus, jqXHR) {
	  console.log(JSON.stringify(data));
      if(data["error"] == undefined) {
	    ok(true);
		matchId = data["matchId"];
	  }else {
		ok(false);
	  }
	  start();	
	},
	error: function(jqXHR, textStatus, errorThrown) {
	  console.log("Access Server Error: " + testStatus + " " + errorThrown);
	}
  });
}

function getNewMatchInfoTest() {
  var url = serverName + "newMatch/";
  url += playerId2;
  url += "?accessSignature=";
  url += accessSignature2;
  url += "&";
  url += "gameId=";
  url += gameId;
  
  $.ajax({
	url: url,
	type: "GET",
    success: function(data, textStatus, jqXHR) {
	  console.log(JSON.stringify(data));
	  if(data["error"] == undefined) {
	    ok(true);
	  }else {
	    ok(false);
	  }
	  start();
	},
	error: function(jqXHR, textStatus, errorThrown) {
	  console.log("Access Server Error: " + testStatus + " " + errorThrown);
	}
  });
}

function getGameStateTest() {
  var url = serverName + "state/";
  url += matchId;
  url += "?playerId=";
  url += playerId2;
  url += "&accessSignature=";
  url += accessSignature2;
	  
  $.ajax({
    url: url,
	type: "GET",
	success: function(data, textStatus, jqXHR) {
	  console.log(JSON.stringify(data));
	  if(data["error"] == undefined) {
	    ok(true);
	  }else {
		ok(false);
	  }
	  start();
	},
	error: function(jqXHR, textStatus, errorThrown) {
	  console.log("Access Server Error: " + testStatus + " " + errorThrown);
	}
  });	
}

function makeMoveInMatchTest() {
  var message = {
    "accessSignature": accessSignature1,
	"operations": [{"value":"wang", "type":"Set", "visibleToPlayerIds":"ALL","key":"name"}],
	"playerIds": [playerId1, playerId2]
  };
  var url = serverName + "matches/" + matchId;
  $.ajax({
    url: url,
    dataType: "json",
    type: "POST",
    data: JSON.stringify(message),
	success: function(data, textStatus, jqXHR) {
	  console.log(JSON.stringify(data));
	  if(data["error"] == undefined) {
	    ok(true);
	  }else {
		ok(false);
	  }
	  start();
	},
	error: function(jqXHR, textStatus, errorThrown) {
	  console.log("Access Server Error: " + testStatus + " " + errorThrown);
	}
  });
}

function ajaxGetMetaInfoOfMultiGames(test_url, expected){

    $.ajax({
        url: serverName + test_url,
        type: 'GET',
        dataType: 'json',
        success: function(data, textStatus, jqXHR) {
            if(data["error"] != "INVALID_URL_PATH_ERROR" &&
                data["error"] !="WRONG_ACCESS_SIGNATURE" &&
                expected == ""
                ) {
                ok(true);
            }
            else if(data["error"] == "INVALID_URL_PATH_ERROR" &&
                expected == "INVALID_URL_PATH_ERROR")
            {
                console.log(data["error"]);
                ok("The url you are requesting is not correct, please refer to server API for correct URL path"
                    == data["details"]);
                console.log(data["details"]);

                ok(true);
            }
            else
            {
                ok(false);
            }

            start();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("ERROR: " + textStatus + " " + errorThrown);
        }
    });

}

function ajaxGetMatchStatsForOneGame(gameid,expected){

    $.ajax({
        url: serverName + "gameinfo/stats?gameId="+gameid,
        type: 'GET',
        dataType: 'json',
        success: function(data, textStatus, jqXHR) {
            console.log(data);
            if(data["error"] != "WRONG_GAME_ID" &&
                expected == ""
                ) {
                ok(true);
            }
            else if(data["error"] == "WRONG_GAME_ID" &&
                expected == "WRONG_GAME_ID")
            {
                ok("The game you are looking for does not exist"
                    == data["details"]);

                ok(true);
            }
            else
            {
                ok(false);
            }

            start();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("ERROR: " + textStatus + " " + errorThrown);
        }
    });

}

function ajaxGiveAGameRating(rating,url,gameId,playerId,accessSignature,expected)
{
    //var rating=3.5;
    var jsonObj = {
        "gameId":gameId,
        "playerId":playerId,
        "accessSignature":accessSignature,
        "rating":rating
    }

    $.ajax({
        url: serverName + url,
        type: 'POST',
        dataType: 'json',
        data: JSON.stringify(jsonObj),
        success: function(data, textStatus, jqXHR) {
            console.log(data);
            if(data["error"] != "WRONG_RATING" &&
                data["error"] != "INVALID_URL_PATH_ERROR" &&
                data["error"] != "PASSWORD_TOO_SHORT" &&
                data["error"] != "WRONG_GAME_ID" &&
                data["error"] != "WRONG_PLAYER_ID" &&
                data["error"] != "WRONG_ACCESS_SIGNATURE" &&
                expected == ""
                ) {
//                accessSignature = data.accessSignature;
//                developerId = data.developerId
                ok(true);
            }
            else if(data["error"] == "WRONG_PLAYER_ID" &&
                expected=="WRONG_PLAYER_ID")
            {
                //console.log(data);
                ok(true);
            }
            else if(data["error"] == "WRONG_GAME_ID" &&
                expected == "WRONG_GAME_ID")
            {
               ok(true);
            }
            else if(data["error"] == "INVALID_URL_PATH_ERROR" &&
                expected == "INVALID_URL_PATH_ERROR")
            {
                ok(true);
            }
            else if(data["error"] == "WRONG_ACCESS_SIGNATURE" &&
                expected == "WRONG_ACCESS_SIGNATURE")
            {
                ok(true);
            }
            else{
                console.log(JSON.stringify(data));
                ok(false);
            }


            start();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("ERROR: " + textStatus + " " + errorThrown);
        }
    });
}



//For Synchronized Mode Test
asyncTest("Register successfully for player1", function() {
	registerTest(1);
});


asyncTest("Register successfully for player2", function() {
	registerTest(2);
});


asyncTest("Player1 enter the queue successfully", function() {
    enterQueueForMatchTest(1);
});

asyncTest("Player2 enter the queue successfully", function() {
	enterQueueForMatchTest(2);
});


//For Asynchronized Mode Test
asyncTest("Register successfully for player1", function() {
	registerTest(1);
});

asyncTest("Register successfully for player2", function() {
	registerTest(2);
});


asyncTest("Insert new match successfully", function() {
    insertNewMatchTest();
});


asyncTest("Get new match Info successfully", function() {
    getNewMatchInfoTest();
});


asyncTest("Get initial game state successfully", function() {
	getGameStateTest();
});

asyncTest("Make a move in a match successfully", function() {
	makeMoveInMatchTest();
});


asyncTest("Get game state successfully", function() {
	getGameStateTest();
});

asyncTest("Get Meta Info Success", function() {
    test_url="gameinfo/all";
    ajaxGetMetaInfoOfMultiGames(test_url,"");
});

asyncTest("Get Meta Info Failed", function() {
    test_url="gameinfo/all/fd";
    ajaxGetMetaInfoOfMultiGames(test_url,"INVALID_URL_PATH_ERROR");
});

asyncTest("Get Match Stats With Right GameId", function() {
    ajaxGetMatchStatsForOneGame(gameId,"");
});

asyncTest("Get Match Stats With Wrong GameId", function() {
    wrongGameId = "746384839393";
    ajaxGetMatchStatsForOneGame(wrongGameId,"WRONG_GAME_ID");
});

asyncTest("Rating Right", function() {

    var rating = 3.5;
    var url="gameinfo/rating";

    ajaxGiveAGameRating(rating,url,gameId,playerId1, accessSignature1,"");
});

asyncTest("Rating Wrong PlayerId", function() {

    var rating = 3.5;
    var url="gameinfo/rating";


    ajaxGiveAGameRating(rating,url,gameId,"48783398474647",
        accessSignature1,"WRONG_PLAYER_ID");
});

asyncTest("Rating Wrong GameId", function() {

    var rating = 3.5;
    var url="gameinfo/rating";


    ajaxGiveAGameRating(rating,url,"57836483393939",playerId1,
        accessSignature1,"WRONG_GAME_ID");
});

asyncTest("Rating Wrong Url", function() {

    var rating = 3.5;
    var url="gameinfo/rating/wrong";


    ajaxGiveAGameRating(rating,url,gameId,playerId1,
        accessSignature1,"INVALID_URL_PATH_ERROR");
});

asyncTest("Rating Wrong accessSignature", function() {

    var rating = 3.5;
    var url="gameinfo/rating";
    var wrong_accessSignature="wrong_accessSignature";


    ajaxGiveAGameRating(rating,url,gameId,playerId1,
        "jfhsjsjdgskjsdjsksks","WRONG_ACCESS_SIGNATURE");
});

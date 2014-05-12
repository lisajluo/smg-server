
var sServer = 'http://smg-server.appspot.com';

var goodPlayer = {
		"email": "player_test@nyu.edu",
		"password": "password"
};
var shortPwdPlayer = {
		"email": "player_test@nyu.edu",
		"password": "pwd"		
};

var playerId;
var accessSignature;

QUnit.config.reorder = false;

function ajaxInsertNewPlayer(expected)
{
  var jsonObj = newPlayer;

  $.ajax({
    url: sServer + "/players", 
    type: 'POST',
    dataType: 'json',
    data: JSON.stringify(jsonObj),
    success: function(data, textStatus, jqXHR) {     
      //console.log(data);
      if(data["error"] != "EMAIL_EXISTS" && data["error"] != "PASSWORD_TOO_SHORT" &&
         expected == ""
         ) {
        accessSignature = data.accessSignature;
        PlayerId = data.playerId
        ok(true);
      }
      else if(data["error"] == "EMAIL_EXISTS" && expected == "EMAIL_EXISTS")
      {
        ok(true);
      }
      else if (data["error"] == "PASSWORD_TOO_SHORT" && expected == "PASSWORD_TOO_SHORT")
      {
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
};

function ajaxGetPlayerInfo(player, target, sig, expected){
	$.ajax({
		url: sServer+'/playerInfo?playerId='+player+'&targetId='+target+'&accessSignature='+sig,
		type: 'GET', 
		dataType: 'json', 
		success: function(data, textStatus, jqXHR){
			//console.log(data);
			if(data["error"]=="WRONG_PLAYER_ID" && expected == "WRONG_PLAYER_ID"){
				ok(true);
			}
			else if(data["error"]=="WRONG_ACCESS_SIGNATURE"
				&& expected == "WRONG_ACCESS_SIGNATURE"){
				ok(true);
			}
			else if(data["error"]=="WRONG_TARGET_ID" && expected == "WRONG_TARGET_ID"){
				ok(true);
			}
			else if(data != null && expected == ""){
				ok(true);
			}
			else{
				ok(false);
			}
			start();	
		},
	error: function(jqXHR, textStatus, errorThrown){
		console.error("ERROR: "+textStatus +" "+errorThrown);
	}
	});
};

function ajaxGetAllGameList(expected)
{
	$.ajax({
		url: sServer + "/gameinfo/all", 
		type: 'GET',
		dataType: 'json',
		success: function(data, textStatus, jqXHR) {     
			//console.log(data);
			totalNum = data.length;
			game = data[0];
			console.log(game);
			if (expected == "") {
				ok(true);
			} else {
				ok(false);
			}
			start();
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.error("ERROR: " + textStatus + " " + errorThrown);
		}
	});
}

function ajaxGetHistoryDetail(player, target, game, sig, expected){
	$.ajax({
		url: sServer+'/history?playerId='+player+'&targetId='+target+'&gameId='+game+'&accessSignature='+sig,
		type: 'GET', 
		dataType: 'json',
		success: function(data, textStatus, jqXHR){
			console.log(data);
			if(data["error"]=="WRONG_PLAYER_ID" && expected == "WRONG_PLAYER_ID"){
				ok(true);
			}
			else if(data["error"]=="WRONG_ACCESS_SIGNATURE" && expected == "WRONG_ACCESS_SIGNATURE"){
				ok(true);
			}
			else if(data["error"]=="WRONG_TARGET_ID" && expected == "WRONG_TARGET_ID"){
				ok(true);
			}
			else if(data["error"]=="WRONG_GAME_ID" && expected == "WRONG_GAME_ID"){
				ok(true);
			}
			else if(data != null && expected == ""){
				ok(true);
			}
			else{
				ok(false);
			}
			start();	
		},
	error: function(jqXHR, textStatus, errorThrown){
		console.error("ERROR: "+textStatus +" "+errorThrown);
	}
	});
};

function ajaxGetHistoryList(player, target, sig, expected){
	$.ajax({
		url: sServer+'/playerAllGame?playerId='+player+'&targetId='+target+'&accessSignature='+sig,
		type: 'GET', 
		dataType: 'json',
		success: function(data, textStatus, jqXHR){
			console.log(data);
			if(data["error"]=="WRONG_PLAYER_ID" && expected == "WRONG_PLAYER_ID"){
				ok(true);
			}
			else if(data["error"]=="WRONG_ACCESS_SIGNATURE" && expected == "WRONG_ACCESS_SIGNATURE"){
				ok(true);
			}
			else if(data["error"]=="WRONG_TARGET_ID" && expected == "WRONG_TARGET_ID"){
				ok(true);
			}
			else if(data["error"]=="WRONG_GAME_ID" && expected == "WRONG_GAME_ID"){
				ok(true);
			}
			else if(data != null && expected == ""){
				ok(true);
			}
			else{
				ok(false);
			}
			start();	
		},
	error: function(jqXHR, textStatus, errorThrown){
		console.error("ERROR: "+textStatus +" "+errorThrown);
	}		
	});
};

function ajaxGetGameInfo(player, target, game, sig, expected){
	$.ajax({
		url: sServer+'/history?playerId='+player+'&targetId='+target+'&gameId='+game+'&accessSignature='+sig,
		type: 'GET', 
		dataType: 'json',
		success: function(data, textStatus, jqXHR){
			console.log(data);
			if(data["error"]=="WRONG_PLAYER_ID" && expected == "WRONG_PLAYER_ID"){
				ok(true);
			}
			else if(data["error"]=="WRONG_ACCESS_SIGNATURE" && expected == "WRONG_ACCESS_SIGNATURE"){
				ok(true);
			}
			else if(data["error"]=="WRONG_TARGET_ID" && expected == "WRONG_TARGET_ID"){
				ok(true);
			}
			else if(data["error"]=="WRONG_GAME_ID" && expected == "WRONG_GAME_ID"){
				ok(true);
			}
			else if(data != null && expected == ""){
				ok(true);
			}
			else{
				ok(false);
			}
			start();	
		},
	error: function(jqXHR, textStatus, errorThrown){
		console.error("ERROR: "+textStatus +" "+errorThrown);
	}
	});
};

function ajaxPostRate(gameId, playerId, accessSignature, rating, expected)
{
	var jsonObj = {
		"gameId": gameId,
		"playerId": playerId,
		"accessSignature": accessSignature,
		"rating": rating
	}
	$.ajax({
		url: sServer + "/gameinfo/rating", 
		type: 'POST',
		dataType: 'json',
		data: JSON.stringify(jsonObj),
		success: function(data, textStatus, jqXHR) {     
		console.log(data);
		if ( data["error"] != "WRONG_RATING" &&
			data["error"] != "WRONG_GAME_ID" &&
			data["error"] != "WRONG_ACCESS_SIGNATURE" &&
			expected == ""
		) {
			ok(true);
		}
		else if(data["error"] == "WRONG_RATING" &&
			expected == "WRONG_RATING")
		{
			ok(true);
		}
		else if(data["error"] == "WRONG_GAME_ID" &&
			expected == "WRONG_GAME_ID")
		{
			ok(true);
		}
		else if(data["error"] == "WRONG_ACCESS_SIGNATURE" &&
			expected == "WRONG_ACCESS_SIGNATURE")
		{
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
};

function ajaxGetGameStat(id, expected)
{
	$.ajax({
		url: sServer + "/gameinfo/stats?gameId=" + id, 
		type: 'GET',
		dataType: 'json',
		success: function(data, textStatus, jqXHR) {     
			console.log(data);
			if (data["error"] == "NO_MATCH_RECORDS" &&
				expected == "NO_MATCH_RECORDS")
			{
				ok(true);
			} else if (data["error"] == "WRONG_GAME_ID" &&
				expected == "WRONG_GAME_ID")
			{
				ok(true);
			} else if (expected == "") {
				ok(true);
			} else {
				ok(false);
			}
		    start();
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.error("ERROR: " + textStatus + " " + errorThrown);
		}
	});
};

function ajaxDeletePlayer(id, sig, expected)
{
	$.ajax({
		url: sServer + "/players/" + id + "?accessSignature=" + sig, 
		type: 'DELETE',
		dataType: 'json',
		success: function(data, textStatus, jqXHR) {     
			console.log(data);
			if (data["error"] == "WRONG_PLAYER_ID" &&
				expected == "WRONG_PLAYER_ID")
			{
				ok(true);
			} else if (data["error"] == "WRONG_ACCESS_SIGNATURE" &&
				expected == "WRONG_ACCESS_SIGNATURE")
			{
				ok(true);
			} else if (data["success"] == "DELETED_PLAYER" && expected == "") {
				ok(true);
			} else {
				ok(false);
			}
		    start();
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.error("ERROR: " + textStatus + " " + errorThrown);
		}
	});
};

asyncTest("Insert New User Success", function() {
	var index = Math.floor(1000 * Math.random());
	newPlayer = goodPlayer;
	newPlayer.email = newPlayer.email + index;
    ajaxInsertNewPlayer("");
});

asyncTest("Insert New User With Existed Email", function() {
	newPlayer = goodPlayer;
    ajaxInsertNewPlayer("EMAIL_EXISTS");
});

asyncTest("Insert New User With Short Password", function() {
	newPlayer = shortPwdPlayer;
    ajaxInsertNewPlayer("PASSWORD_TOO_SHORT");
});

asyncTest( "Get Player's Info With WRONG_ACCESS_SIGNATURE", function() {
	ajaxGetPlayerInfo(PlayerId, PlayerId, "", "WRONG_ACCESS_SIGNATURE");
});

asyncTest( "Get Player's Info With WRONG_PLAYER_ID", function() {
	ajaxGetPlayerInfo("", PlayerId, accessSignature, "WRONG_PLAYER_ID");
});

asyncTest( "Get Player's Info With WRONG_TARGET_ID", function() {
	ajaxGetPlayerInfo(PlayerId, "", accessSignature, "WRONG_TARGET_ID");
});

asyncTest( "Get Player's Info Correctly", function() {
	ajaxGetPlayerInfo(PlayerId, PlayerId, accessSignature, "");
});

asyncTest( "Get All Game Correctly", function() {
	ajaxGetAllGameList("");
});

asyncTest( "Get Player's History Detail With WRONG_TARGET_ID", function() {
	ajaxGetHistoryDetail(PlayerId, "", game.gameId, accessSignature, "WRONG_TARGET_ID");
});

asyncTest("Get Player's History Detail With WRONG_PLAYER_ID", function() {
	ajaxGetHistoryDetail("", PlayerId, game.gameId, accessSignature, "WRONG_PLAYER_ID");
});

asyncTest( "Get Player's History Detail With WRONG_ACCESS_SIGNATURE", function() {
	ajaxGetHistoryDetail(PlayerId, PlayerId, game.gameId, "", "WRONG_ACCESS_SIGNATURE");
});

asyncTest( "Get Player's History Detail Correctly", function() {
	ajaxGetHistoryDetail(PlayerId, PlayerId, game.gameId, accessSignature, "");
});

asyncTest( "Get History List With WRONG_TARGET_ID", function() {
	ajaxGetHistoryList(PlayerId, "", accessSignature, "WRONG_TARGET_ID");
});

asyncTest( "Get History List With WRONG_PLAYER_ID", function() {
	ajaxGetHistoryList("", PlayerId, accessSignature, "WRONG_PLAYER_ID");
});

asyncTest( "Get History List With WRONG_ACCESS_SIGNATURE", function() {
	ajaxGetHistoryList(PlayerId, PlayerId, "", "WRONG_ACCESS_SIGNATURE");
});

asyncTest( "Get History List Correctly", function() {
	ajaxGetHistoryList(PlayerId, PlayerId, accessSignature, "");
});

asyncTest( "Get Game Token With WRONG_TARGET_ID", function() {
	ajaxGetGameInfo(PlayerId, "", game.gameId, accessSignature, "WRONG_TARGET_ID");
});

asyncTest( "Get Game Token With WRONG_PLAYER_ID", function() {
	ajaxGetGameInfo("", PlayerId, game.gameId, accessSignature, "WRONG_PLAYER_ID");
});

asyncTest( "Get Game Token With WRONG_ACCESS_SIGNATURE", function() {
	ajaxGetGameInfo(PlayerId, PlayerId, game.gameId, "", "WRONG_ACCESS_SIGNATURE");
});

asyncTest( "Get Game Token With WRONG_GAME_ID", function() {
	ajaxGetGameInfo(PlayerId, PlayerId, "", accessSignature, "WRONG_GAME_ID");
});

asyncTest( "Get Game Token Correctly", function() {
	ajaxGetGameInfo(PlayerId, PlayerId, game.gameId, accessSignature, "");
});

asyncTest("Rate A Game Correctly", function() {
    var goodRate = "5.0";
    ajaxPostRate(game.gameId, PlayerId, accessSignature, goodRate, "");
});

asyncTest("Rate A Game With WRONG_RATING", function() {
    var badRate = "500";
    ajaxPostRate(game.gameId, PlayerId, accessSignature, badRate, "WRONG_RATING");
});

asyncTest("Rate A Game With WRONG_GAME_ID", function() {
    var goodRate = "5.0";
    ajaxPostRate("1111111", PlayerId, accessSignature, goodRate, "WRONG_GAME_ID");
});

asyncTest("Rate A Game With WRONG_ACCESS_SIGNATURE", function() {
    var goodRate = "5.0";
    ajaxPostRate(game.gameId, PlayerId, "", goodRate, "WRONG_ACCESS_SIGNATURE");
});

asyncTest("Get Game Stat Correctly", function() {
    ajaxGetGameStat(game.gameId, "");
});

asyncTest("Get Game Stat With WRONG_GAME_ID", function() {
    ajaxGetGameStat("1234", "WRONG_GAME_ID");
});

asyncTest("Delete Player With WRONG_PLAYER_ID", function() {
	ajaxDeletePlayer("", accessSignature, "WRONG_PLAYER_ID");
});

asyncTest("Delete Player With WRONG_ACCESS_SIGNATURE", function() {
	ajaxDeletePlayer(PlayerId, "", "WRONG_ACCESS_SIGNATURE");
});

asyncTest("Delete Player Correctly", function() {
	ajaxDeletePlayer(PlayerId, accessSignature, "");
});
//var domainUrl = "http://localhost:8888";
var domainUrl = "http://smg-server.appspot.com";
/*
var playerIds = [];
var accessSignatures = [];
var gameId = 0;
var matchId = 0;
var developerId = 0;
var developerAS = "";
*/

QUnit.config.reorder = false;

var goodEmail = ""
var goodPassword = "password";

var goodGame = "";
var goodDescription = "A Description";
var goodDescription2 = "A 2nd Description";
var goodUrl = "www.fungame.com";

var accessSignature = "";
var developerId = "";

var gameId = "";

function ajaxCreateNewPlayer()
{
  var jsonObj = {
    "email":goodEmail,
    "password":goodPassword
  }

  $.ajax({
    url: domainUrl + "/developers", 
    type: 'POST',
    dataType: 'json',
    data: JSON.stringify(jsonObj),
    success: function(data, textStatus, jqXHR) {     
      console.log(data);
      if(data["error"] != "EMAIL_EXISTS" &&
         data["error"] != "MISSING_INFO" &&
         data["error"] != "PASSWORD_TOO_SHORT"
         ) {
        accessSignature = data.accessSignature;
        developerId = data.developerId
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

function ajaxDeletePlayer()
{
  $.ajax({
    url: domainUrl + "/developers/" + developerId + "?accessSignature=" + accessSignature, 
    type: 'DELETE',
    dataType: 'json',
    success: function(data, textStatus, jqXHR) {     
      console.log(data);
      if(data["error"] != "WRONG_ACCESS_SIGNATURE" &&
         data["error"] != "WRONG_DEVELOPER_ID"
         ) {
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

function ajaxCreateNewGame(name, description, url, access, expected)
{
  var jsonObj = {
    "developerId":developerId,
    "accessSignature":access,
    "gameName":name,
    "description":description,
    "url":url,
    "width":"",
    "height":""
  }

  $.ajax({
    url: domainUrl + "/games", 
    type: 'POST',
    dataType: 'json',
    data: JSON.stringify(jsonObj),
    success: function(data, textStatus, jqXHR) {     
      console.log(data);
      if(data["error"] != "GAME_EXISTS" &&
         data["error"] != "MISSING_INFO" &&
         data["error"] != "WRONG_ACCESS_SIGNATURE" &&
         expected == ""
         ) {
        gameId = data["gameId"];
        ok(true);
      }
      else if(data["error"] == "GAME_EXISTS" &&
              expected == "GAME_EXISTS")
      {
        ok(true);
      }
      else if(data["error"] == "MISSING_INFO" &&
              expected == "MISSING_INFO")
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
}

function ajaxGetGame(id, name, description, url, expected)
{
  $.ajax({
    url: domainUrl + "/games/" + id, 
    type: 'GET',
    dataType: 'json',
    success: function(data, textStatus, jqXHR) {     
      console.log(data);
      if(data["error"] != "WRONG_GAME_ID" &&
         expected == ""
         ) {
        ok(name == data["gameName"]);
        ok(description == data["description"]);
        ok(url == data["url"]);
        ok(true);
      }
      else if(data["error"] == "WRONG_GAME_ID" &&
              expected == "WRONG_GAME_ID")
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
}

function ajaxUpdateGame(description, id, expected)
{
  var jsonObj = {
    "developerId":id,
    "accessSignature":accessSignature,
    "description":description
  }

  $.ajax({
    url: domainUrl + "/games/" + gameId, 
    type: 'PUT',
    dataType: 'json',
    data: JSON.stringify(jsonObj),
    success: function(data, textStatus, jqXHR) {     
      console.log(data);
      if(data["error"] != "GAME_EXISTS" &&
         data["error"] != "MISSING_INFO" &&
         data["error"] != "WRONG_ACCESS_SIGNATURE" &&
         data["error"] != "WRONG_DEVELOPER_ID" &&
         expected == ""
         ) {
        ok(true);
      }
      else if(data["error"] == "GAME_EXISTS" &&
              expected == "GAME_EXISTS")
      {
        ok(data["description"] = description);
        ok(true);
      }
      else if(data["error"] == "MISSING_INFO" &&
              expected == "MISSING_INFO")
      {
        ok(true);
      }
      else if(data["error"] == "WRONG_ACCESS_SIGNATURE" &&
              expected == "WRONG_ACCESS_SIGNATURE")
      {
        ok(true);
      }
      else if(data["error"] == "WRONG_DEVELOPER_ID" &&
              expected == "WRONG_DEVELOPER_ID")
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
}

function ajaxGetAllGames(expected)
{
  $.ajax({
    url: domainUrl + "/gameinfo/all?developerId=" + developerId + "&accessSignature=" + accessSignature, 
    type: 'GET',
    dataType: 'json',
    success: function(data, textStatus, jqXHR) {     
      console.log(data);
      if(data["error"] != "WRONG_GAME_ID" &&
         data["error"] != "WRONG_DEVELOPER_ID" &&
         expected == ""
         ) {
        ok(goodGame == data[0]["gameName"]);
        ok(goodDescription2 == data[0]["description"]);
        ok(goodUrl == data[0]["url"]);
        ok(true);
      }
      else if(data["error"] == "WRONG_GAME_ID" &&
              expected == "WRONG_GAME_ID")
      {
        ok(true);
      }
      else if(data["error"] == "WRONG_DEVELOPER_ID" &&
              expected == "WRONG_DEVELOPER_ID")
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
}

function ajaxDeleteGame()
{
  $.ajax({
    url: domainUrl + "/games/" + gameId + "?developerId=" + developerId + "&accessSignature=" + accessSignature, 
    type: 'DELETE',
    dataType: 'json',
    success: function(data, textStatus, jqXHR) {     
      console.log(data);
      if(data["error"] != "WRONG_ACCESS_SIGNATURE" &&
         data["error"] != "WRONG_DEVELOPER_ID"
         ) {
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

asyncTest("Insert New User Success", function() {
    var a = Math.floor(1000*Math.random());
    var b = Math.floor(1000*Math.random());
    var c = Math.floor(1000*Math.random());
    goodEmail = a.toString() + '@' + b.toString() + '.' + c.toString();
    ajaxCreateNewPlayer();
});

asyncTest("Create Game Successful", function() {
    var a = Math.floor(1000*Math.random());
    goodGame = "Fun Game " + a.toString();
    ajaxCreateNewGame(goodGame, goodDescription, goodUrl, accessSignature, "");
});

asyncTest("Create Game Failed Game Exists", function() {
    ajaxCreateNewGame(goodGame, goodDescription, goodUrl, accessSignature, "GAME_EXISTS");
});

asyncTest("Create Game Failed Missing Info", function() {
    ajaxCreateNewGame("Random Game", undefined, goodUrl, accessSignature, "MISSING_INFO");
});

asyncTest("Create Game Failed Wrong Access Signature", function() {
    ajaxCreateNewGame("Random Game", "Random Description", "Random Url", "2314", "WRONG_ACCESS_SIGNATURE");
});

asyncTest("Get Game Successful", function() {
    ajaxGetGame(gameId, goodGame, goodDescription, goodUrl, "");
});

asyncTest("Get Game Failed Wrong Id", function() {
    ajaxGetGame("1234", goodGame, goodDescription, goodUrl, "WRONG_GAME_ID");
});

asyncTest("Update Game Successful", function() {
    ajaxUpdateGame(goodDescription2, developerId, "");
});

asyncTest("Update Game Failed Wrong Developer Id", function() {
    ajaxUpdateGame(goodDescription2, "1234", "WRONG_DEVELOPER_ID");
});

asyncTest("Get All Developer Games Successful", function() {
    ajaxGetAllGames("");
});

asyncTest("Delete Game Successful", function() {
    ajaxDeleteGame();
});

asyncTest("Delete User Successful", function() {
    ajaxDeletePlayer();
});


var domainUrl = "http://localhost:8888";
//var domainUrl = "http://1-dot-smg-server.appspot.com";
var playerIds = [];
var accessSignatures = [];
var gameId = 0;
var matchId = 0;
var developerId = 0;
var developerAS = "";
var channel_token = "";

function ajaxCallNewPlayer(url, userMail) {
  var jsonObj = { 
      "email":userMail,
      "password":"foobar",
      "firstname":"foo",
      "lastname":"bar",
      "nickname":"foobar" }
  $.ajax({
    url: url, 
    type: 'POST',
    dataType: 'json',
    data: JSON.stringify(jsonObj),
    success: function(data, textStatus, jqXHR) {     
      console.log(data);
      if(data["error"] == undefined) {
          playerIds.push(parseInt(data['playerId']));
          accessSignatures.push(data["accessSignature"]);
      }
      ok(true);
      start();
    },
    error: function(jqXHR, textStatus, errorThrown) {
      console.error("ERROR: " + textStatus + " " + errorThrown);
    }
    });
}

asyncTest("Add Player1", function() {
    ajaxCallNewPlayer( domainUrl+"/players", "aaaa@163.com");
});

function ajaxCallNewDeveloper(url) {
  var jsonObj =  { "email": "blahblaasdaaaasdfsffh@bladsfah.com", 
      "password": "soldkfjlaskdf",
      "nickname": "blahb lah blah",
      "whatever": "some thing should be deleted"
    }

  $.ajax({
    url: url, 
    type: 'POST',
    dataType: 'json',
    data: JSON.stringify(jsonObj),
    success: function(data, textStatus, jqXHR) {     
        console.log(data);
        if(data["error"] == undefined) {
            developerId = parseInt(data['developerId']);
            developerAS = data["accessSignature"];
        }
        ok(true);
        start();
    },
    error: function(jqXHR, textStatus, errorThrown) {
      console.error("ERROR: " + textStatus + " " + errorThrown);
    }
    });
}

asyncTest("Add Developer", function() {
    ajaxCallNewDeveloper( domainUrl+"/developers");
});

function ajaxCallNewGame(url, dId, dAs) {
  var jsonObj = { "key": "value2" };
  $.ajax({
    url: url, 
    type: "POST",
    data: {
        "gameName": "blaaaaaaaaaGameName",
        "url" : "www.foo.com",
        "description": "This game is actually quite self-explanatory",
        "width" : 50,
        "height" : 100,
        "pic" : "{\"icon\" : \"www.google.com\" , \"screenshots\" : [\"www.test1.com\",\"www.test2.com\"]}",
        "developerId" : dId,
        "accessSignature": dAs
    },
    success: function(data, textStatus, jqXHR) {
        console.log(data);
        if(data["error"] == undefined) {
            gameId = JSON.parse(data)["gameId"];
        }
        ok(true);
        start();
    },
    error: function(jqXHR, textStatus, errorThrown) {
        console.log(jqXHR);
        ok(true);
        start();
    }});
}

asyncTest("Add Game", function() {
    ajaxCallNewGame( domainUrl+"/games", developerId, developerAS );
});


function ajaxCallEnqueue(url) {
	var as = accessSignatures[0];
	var pid = playerIds[0];
	var gid = gameId;
	var jsonObj = {
        "accessSignature": as.toString(),
        "playerId" : pid.toString(),
        "gameId": gid.toString()
    };
  $.ajax({
    url: url + "/queue", 
    type: "POST",
    dataType: 'json',
    data: JSON.stringify(jsonObj),
    success: function(data, textStatus, jqXHR) {
        console.log(data);
		if(data["error"] == undefined) {
            channel_token = (data)["channelToken"];
        }
        ok(true);
        start();
    },
    error: function(jqXHR, textStatus, errorThrown) {
        console.log(jqXHR);
        ok(true);
        start();
    }});
}

asyncTest("Enqueue", function() {
    ajaxCallEnqueue(domainUrl);
});

function ajaxCallDelPlayer(url) {
  $.ajax({
    url: url, 
    type: "DELETE",
    crossDomain: true,
    success: function(data, textStatus, jqXHR) {
      console.log(data);
      ok(true);
      start();
    },
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }});
}

asyncTest("Delete Player1", function() {
    ajaxCallDelPlayer( domainUrl+"/players/"+playerIds[0]+"?accessSignature="+accessSignatures[0]);
});

function ajaxCallDelGame(url) {
  $.ajax({
    url: url, 
    type: "DELETE",
    crossDomain: true,
    success: function(data, textStatus, jqXHR) {
      console.log(data);
      ok(true);
      start();
    },
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }});
}

asyncTest("Delete Game", function() {
   ajaxCallDelGame( domainUrl+"/games/"+gameId+"?developerId="+developerId+"&accessSignature="+developerAS);
});

function ajaxCallDelDeveloper(url) {
  $.ajax({
    url: url, 
    type: "DELETE",
    crossDomain: true,
    success: function(data, textStatus, jqXHR) {
      console.log(data);
      ok(true);
      start();
    },
    error: function(jqXHR, textStatus, errorThrown) {
      alert("ERROR: " + textStatus + " " + errorThrown);
    }});
}

asyncTest("Delete Developer", function() {
    ajaxCallDelDeveloper( domainUrl+"/developers/"+developerId+"?accessSignature="+developerAS);
});

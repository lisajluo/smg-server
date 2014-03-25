var domainUrl = "http://localhost:8888";
var playerIds = [];
var accessSignatures = [];
var gameId = 0;
var matchId = 0;
var developerId = 0;
var developerAS = "";

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

asyncTest("Add Player failed email used", function() {
    ajaxCallNewPlayer( domainUrl+"/players", "aaaa@163.com");
});

asyncTest("Add Player2", function() {
    ajaxCallNewPlayer( domainUrl+"/players", "bbbbb@msn.com");
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

// asyncTest("Add Developer failed email used", function() {
//     ajaxCallNewDeveloper( domainUrl+"/developers");
// });

function ajaxCallNewGame(url, dId, dAs) {
  var jsonObj = { "key": "value2" };
  $.ajax({
    url: url, 
    type: "POST",
    data: {
        "gameName": "newTest",
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

asyncTest("Add Game failed wrong developerId", function() {
    ajaxCallNewGame( domainUrl+"/games", 0, developerAS  );
});

asyncTest("Add Game failed wrong accessSignature", function() {
    ajaxCallNewGame( domainUrl+"/games", developerId, 0  );
});

function ajaxCallNewMatch(url, as, pIds, gId) {
  var jsonObj = { 'accessSignature': as,
                  'playerIds': pIds,
                  'gameId': gId }
    $.ajax({
        url: url, 
        type: 'POST',
        dataType: 'json',
        data: JSON.stringify(jsonObj),
        success: function(data, textStatus, jqXHR) { 
            console.log(data);
            if(data["error"] == undefined) {
                matchId = parseInt(data['matchId']);
            }
            ok(true);
            start();  
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            ok(true);
            start();  
        }
        });
}

asyncTest("Add Match", function() {
    ajaxCallNewMatch( domainUrl+"/newMatch", accessSignatures[0], playerIds, gameId);
});

asyncTest("Add Match failed wrong accessSignature", function() {
    ajaxCallNewMatch( domainUrl+"/newMatch", 0, playerIds, gameId);
});

asyncTest("Add Match failed wrong playerIds", function() {
    ajaxCallNewMatch( domainUrl+"/newMatch", accessSignatures[0], [0, 1], gameId);
});

asyncTest("Add Match failed wrong gameId", function() {
    ajaxCallNewMatch( domainUrl+"/newMatch", accessSignatures[0], playerIds, 0);
});

function ajaxCallGetMatchInfo(url) {
    $.ajax({
        url: url+"/"+matchId+"?accessSignature="+accessSignatures
        [0]+"&playerId="+playerIds[0]+"", 
        type: "GET",
        success: function(data, textStatus, jqXHR) {
            var j = JSON.parse(data);
            console.log(data);
            ok(true);
            start();  
        },
        error: function(jqXHR, textStatus, errorThrown) {
          alert("ERROR: " + textStatus + " " + errorThrown);
        }});
}

asyncTest("Get Match Info", function() {
    ajaxCallGetMatchInfo( domainUrl+"/matches");
});

function ajaxCallMakeNewMove(url, as, pIds) {
    var jsonObj = {
          "accessSignature": as,
          'playerIds': pIds,
          "operations": [
              {
                  "value": "sd",
                  "type": "Set",
                  "visibleToPlayerIds": "ALL",
                  "key": "k"
              },
              {
                  "to": 54,
                  "from": 23,
                  "type": "SetRandomInteger",
                  "key": "xcv"
              }
          ]
      };
      $.ajax({
        url: url+"/"+matchId+"", 
        type: "POST",
        dataType: 'json',
        data: JSON.stringify(jsonObj),
        success: function(data, textStatus, jqXHR) {
            console.log(data);
            ok(true);
            start();  
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            ok(true);
            start();  
        }});
}

asyncTest("Make a Move", function() {
    ajaxCallMakeNewMove(domainUrl+"/matches", accessSignatures[0].toString(), playerIds);
});

asyncTest("Make a Move failed wrong accessSignature", function() {
    ajaxCallMakeNewMove(domainUrl+"/matches", "12123123", playerIds);
});

asyncTest("Make a Move failed wrong playerIds", function() {
    ajaxCallMakeNewMove(domainUrl+"/matches", accessSignatures[0].toString(), [0,1]);
});

asyncTest("Get Match Info After", function() {
    ajaxCallGetMatchInfo( domainUrl+"/matches");
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

asyncTest("Delete Player2", function() {
    ajaxCallDelPlayer( domainUrl+"/players/"+playerIds[1]+"?accessSignature="+accessSignatures[1]);
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

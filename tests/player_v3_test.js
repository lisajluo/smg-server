//Test under jQuery and QUnit
var StaticURL = "http://2.smg-server.appspot.com"; 

function joinObject(dst,src) {
  for (var k in src) {
    if (src[k] != undefined){
      dst[k] = src[k];
    }
  }
  return dst;
}


//player id: stored in str
var playerId = [];

function addPlayerId(id) {
  playerId.push(id);
}

function getPlayerId(index) {
  if (playerId[index] != undefined) {
    return playerId[index];
  } else {
    return playerId[playerId.length-1];
  }
}

function testEqual(testing, expected, title) {
  deepEqual( testing, expected, "We expect two objects are equal.");
}

//Make AJAX call and return the responce object
function testAjax(method, testUrl, sendingMessage, before) {
  if (before != undefined) {
    for (var i = 0; i < before.length; i ++){
      var beforeOb = before[i];
      var t = syncAjax(
        beforeOb["method"],
        beforeOb["testUrl"],
        beforeOb["sendingMessage"],
        beforeOb["requiredKeys"]
      );
      joinObject(sendingMessage,t);
    }
  }
  var temp = {}
  $.ajax({
    url: testUrl,
    dataType: 'json',
      type:method,
      data:sendingMessage,
      success: function(data) {
        parsedData = $.parseJSON(data);
        temp = parsedData;
      },
      error: function(data) {
        parsedData = $.parseJSON(data);
        temp = parsedData;
      }
  });
  return temp;
}

//AjaxCall for before/after testing
//requiredKeys is the array of field name that we need to acquire from this call;
//return an object with required information
//This call is synchronous, since the following call depend on the result of this
function syncAjax(method, testUrl, sendingMessage, requiredKeys){
  var temp = {};
  $.ajax({
    url: connecttUrl,
    dataType: 'json',
    type:method,
    data:sendingMessage,
    async: false,
    success: function(data) {
      parsedData = $.parseJSON(data);
      for (var i = 0; i < requiredKeys; i ++) {
        var k = requiredKeys[i];
        if (data[k] != undefined) {
          temp[k] = data[k];
        }
      }
    },
    error: function(data) {
      parsedData = $.parseJSON(data);
      for (var i = 0; i < requiredKeys; i ++) {
        var k = requiredKeys[i];
        if (data[k] != undefined) {
          temp[k] = data[k];
        }
      }
    }
  });
  return temp;
}

function runTestCase(c) {
  var ob  = {};
  test(c["testTitle"], function() {
    stop();
    ob = testAjax(c["method"],
      StaticURL+c["testUrl"],
      c["sendingMessage"],
      c["before"]
      );
    if (ob["playerId"] != undefined) {
      playerId.push(ob["playerId"]);
    }
    var expectedUnknown = c["expectedUnknownFields"];
    var expectedMessage = c["expectedMessage"];
    for (var i = 0; i < expectedUnknown.length; i ++) {
      var k = expectedUnknown[i];
      if (ob[k] != undefined) {
        expectedMessage[k] = ob[k];
      }
    }
    setTimeout(function() {
      testEqual(ob,expectedMessage,c["testTitle"]);
      start();
    },1500);
  })
}





/*Example of test driver
  Test cases should clearup the environment after testing unless impossible.
  Otherwise tests might succeed at the first time then fail later.

  Some fields are expected but the value is unknown, use expectedUnknownFields to pass these fields
  
  API to modify: //need further discuss
  
  POST /friend managing friend list
  
  send friend request/accept friend request
  POST /friend/{ownPlayerID}
  {"accessSignature":...,"Action":"ADD","playerId":...}
  Return {"success":"REQUEST_SENT"} //success
  Return {"error":"WRONG_PLAYER_ID"} //no such user
  Return {"error":"ALREADY_FRIEND"} //already friend
  Return {"error":"ACTION_DENY"} //action denied (blocked user)
  
  remove friend
  POST /friend/{ownPlayerID}
  {"accessSignature":...,"Action":"REMOVE","playerId":...}
  Return {"success":"REMOVED"} //success
  Return {"error":"WRONG_PLAYER_ID"} //no such friend
  
  get friend list
  GET /friend/{ownPlayerID}
  {"accessSignature":...,"Action":"LIST"}
  Return {"friends":[...]} //success
  
  
  
*/

//TODO manage testcase dependency
function testPlayerV1() {
  var testCases = [
  //Add a player
  {
    "method":"POST",
    "testUrl":"/players",
    "sendingMessage":{
      "email":"blablabla@gmail.com",
      "password":"foobar",
      "firstName":"foo",
      "lastName":"bar",
      "nickName":"foobar",
    },
    "expectedMessage":{
    },
    "expectedUnknownFields":["playerId"],
    "testTitle":"Inserting a new player",
  },
  //Add the same player, should return an error
  {
    "method":"POST",
    "testUrl":"/players",
    "sendingMessage":{
      "email":"blablabla@gmail.com",
      "password":"foobar",
      "firstName":"foo",
      "lastName":"bar",
      "nickName":"foobar",
    },
    "expectedMessage":{
      "error": "EMAIL_EXISTS"
    },
    "expectedUnknownFields":[],
    "testTitle":"Inserting a new player with existing email",
  },
  //Add another player
  {
    "method":"POST",
    "testUrl":"/players",
    "sendingMessage":{
      "email":"blablablabla@gmail.com",
      "password":"foobarfoobar",
      "firstName":"foofoo",
      "lastName":"barbar",
      "nickName":"foobarfoobar",
    },
    "expectedMessage":{
    },
    "expectedUnknownFields":["playerId"],
    "testTitle":"Inserting a new player",
  },
  ];

  //static URL definition

  //Waiting time parameter
  var waitTime = 0;
  var waitTimeInc = 2000;

  for (var i = 0; i < testCases.length; i ++) {
    var c = testCases[i];
    setTimeout(runTestCase(c), waitTime);
    waitTime += waitTimeInc;
  }

  //Wait Until user is created
  setTimeout( function() {
  var testCasesDependent = [
  //Login player
  //TODO get playerId from previous call
  {
    "method":"GET",
    "testUrl":"/players/"+getPlayerId(0),
    "sendingMessage":{
      "password":"foobar",
    },
    "expectedMessage":{
      "email":"blablabla@gmail.com",
    },
    "expectedUnknownFields":["accessSignature"],
    "testTitle":"Login player",
  },
  //Login player with wrong password
  {
    "method":"GET",
    "testUrl":"/players/"+getPlayerId(0),
    "sendingMessage":{
      "password":"foobarla",
    },
    "expectedMessage":{
      "error": "WRONG_PASSWORD",
    },
    "expectedUnknownFields":[],
    "testTitle":"Login player with wrong password",
  },
  //Loginplayer with wrong id
  {
    "method":"GET",
    "testUrl":"/players/12345",
    "sendingMessage":{
      "password":"foobar",
    },
    "expectedMessage":{
      "error": "WRONG_PLAYER_ID",
    },
    "expectedUnknownFields":[],
    "testTitle":"Login player with wrong id",
  },
  //Submit a game request
  //TODO: Obtain game Id from previous call
  {
    "method":"POST",
    "testUrl":"/matcher",
    "sendingMessage":{
      "playerIds":getPlayerId(0),
      "gameId":1,
    },
    "expectedMessage":{
    },
    "expectedUnknownFields":["matchId"],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(0),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Delete a player with wrong Id",
  },
  //Update a player Info
  {
    "method":"PUT",
    "testUrl":"/players/"+getPlayerId(0),
    "sendingMessage":{
      "password":"foobar1",
      "firstName":"foo1",
      "lastName":"bar1",
      "nickName":"foobar1",
    },
    "expectedMessage":{
      "success": "UPDATED_PLAYER",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(0),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Update a    info",
  },
  //Update a player Info with wrong accessSignature
  {
    "method":"PUT",
    "testUrl":"/players/"+getPlayerId(0),
    "sendingMessage":{
      "password":"foobar1",
      "firstName":"foo1",
      "lastName":"bar1",
      "nickName":"foobar1",
      "accessSignature":"foobarwrongsignature",
    },
    "expectedMessage":{
      "error": "WRONG_ACCESS_SIGNATURE",
    },
    "expectedUnknownFields":[],
    "testTitle":"Update a player info with wrong accessSignature",
  },
  //Update a player Info with wrong Id
  {
    "method":"PUT",
    "testUrl":"/players/12345",
    "sendingMessage":{
      "password":"foobar1",
      "firstName":"foo1",
      "lastName":"bar1",
      "nickName":"foobar1",
    },
    "expectedMessage":{
      "error": "WRONG_PLAYER_ID",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(0),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Update a player info with wrong Id",
  },
  //Getting a player info
  {
    "method":"GET",
    "testUrl":"/players/"+getPlayerId(0),
    "sendingMessage":{},
    "expectedMessage":{
      "firstName":"foo1",
      "lastName":"bar1",
      "nickName":"foobar1",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(0),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Get player Info",
  },
  //Getting other players info
  {
    "method":"GET",
    "testUrl":"/players/"+getPlayerId(1),
    "sendingMessage":{
      "playerId":getPlayerId(0)
    },
    "expectedMessage":{
      "firstName":"foofoo",
      "lastName":"barbar",
      "nickName":"foobarfoobar",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(0),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Get player Info",
  },
  //Getting a player info with wrong id
  {
    "method":"GET",
    "testUrl":"/players/12345",
    "sendingMessage":{},
    "expectedMessage":{
      "error": "WRONG_PLAYER_ID",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(0),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Get player Info with wrong Id",
  },
  //Send a friend request
  {
    "method":"POST",
    "testUrl":"/friend/"+getPlayerId(0),
    "sendingMessage":{
      "ACTION":"ADD",
      "playerId":getPlayerId(1),
    },
    "expectedMessage":{
      "success": "SEND_FRIEND_REQUEST",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(0),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Send a friend request",
  },
  //Send duplicate friend request
  {
    "method":"POST",
    "testUrl":"/friend/"+getPlayerId(0),
    "sendingMessage":{
      "ACTION":"ADD",
      "playerId":getPlayerId(1),
    },
    "expectedMessage":{
      "error":"ACTION_DENY",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(0),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Send duplicate friend request",
  },
  //Accept a friend request
  {
    "method":"POST",
    "testUrl":"/friend/"+getPlayerId(1),
    "sendingMessage":{
      "ACTION":"ADD",
      "playerId":getPlayerId(0)
    },
    "expectedMessage":{
      "success": "SEND_FRIEND_REQUEST",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(1),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Accept a friend request",
  },
  //add a friend already exist
  {
    "method":"POST",
    "testUrl":"/friend/"+getPlayerId(1),
    "sendingMessage":{
      "ACTION":"ADD",
      "playerId":getPlayerId(0)
    },
    "expectedMessage":{
      "error":"ALREADY_FRIEND",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(1),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"add a friend already exist",
  },
  //add a non-exist player
  {
    "method":"POST",
    "testUrl":"/friend/"+getPlayerId(1),
    "sendingMessage":{
      "ACTION":"ADD",
      "playerId":12345,
    },
    "expectedMessage":{
      "error":"WRONG_PLAYER_ID",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(1),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"add a non-exist player",
  },
  //Get friend list
  {
    "method":"GET",
    "testUrl":"/friend/"+getPlayerId(0),
    "sendingMessage":{
      "ACTION":"LIST",
    },
    "expectedMessage":{
      "friends": [getPlayerId(1)],
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(0),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"get friend list",
  },
  //Remove a friend
  {
    "method":"POST",
    "testUrl":"/friend/"+getPlayerId(1),
    "sendingMessage":{
      "ACTION":"REMOVE",
      "playerId":getPlayerId(0)
    },
    "expectedMessage":{
      "success": "REMOVED",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(1),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Remove a friend",
  },
  //Get friend list after being removed
  {
    "method":"GET",
    "testUrl":"/friend/"+getPlayerId(0),
    "sendingMessage":{
      "ACTION":"LIST",
    },
    "expectedMessage":{
      "friends": [],
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(0),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"get friend list",
  },
  //Remove a non-exist friend
  {
    "method":"POST",
    "testUrl":"/friend/"+getPlayerId(1),
    "sendingMessage":{
      "ACTION":"REMOVE",
      "playerId":getPlayerId(0)
    },
    "expectedMessage":{
      "error":"WRONG_PLAYER_ID",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(1),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Remove a non-exist friend",
  },
  //Deleting a player
  {
    "method":"DELETE",
    "testUrl":"/players/"+getPlayerId(0),
    "sendingMessage":{
    },
    "expectedMessage":{
      "success": "DELETED_PLAYER",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(0),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Delete a player",
  },
  //Deleting a player
  {
    "method":"DELETE",
    "testUrl":"/players/"+getPlayerId(1),
    "sendingMessage":{
    },
    "expectedMessage":{
      "success": "DELETED_PLAYER",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/"+getPlayerId(1),
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Delete a player",
  },
  ];

  for (var i = 0; i < testCasesDependent.length; i ++) {
    var c = testCases[i];
    //wait between test cases
    //setTimeout is async
    setTimeout(runTestCase(c), waitTime);
    waitTime += waitTimeInc;
  }
  }, 5000);

}


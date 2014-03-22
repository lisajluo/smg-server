//Test under jQuery and QUnit
var StaticURL = "http://1.smg-server.appspot.com"; 

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
      data:JSON.stringify(sendingMessage),
      success: function(data) {
        temp = data;
      },
      error: function(data) {
        temp = data;
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
  ];

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
  //Delete a player Info with wrong accessSignature
  {
    "method":"DELETE",
    "testUrl":"/players/"+getPlayerId(0),
    "sendingMessage":{
      "accessSignature":"foobarwrongsignature",
    },
    "expectedMessage":{
      "error": "WRONG_ACCESS_SIGNATURE",
    },
    "expectedUnknownFields":[],
    "testTitle":"Delete a player with wrong accessSignature",
  },
  //Delete a player Info with wrong Id
  {
    "method":"DELETE",
    "testUrl":"/players/12345",
    "sendingMessage":{
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
    "testTitle":"Delete a player with wrong Id",
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


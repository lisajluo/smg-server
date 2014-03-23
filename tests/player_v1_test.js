//Test under jQuery and QUnit
//var StaticURL = "http://1.smg-server.appspot.com"; 
var StaticURL = "http://localhost:8888";
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
var accessSignature = [];

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

function getAccessSignature(index) {
	  if (accessSignature[index] != undefined) {
	    return accessSignature[index];
	  } else {
	    return accessSignature[accessSignature.length-1];
	  }
	}

function testEqual(testing, expected, title) {
  deepEqual( testing, expected, "We expect two objects are equal.");
}

//Make AJAX call and return the responce object
function testAjax(method, testUrl, sendingMessage, before) {
  if (false && before != undefined) {
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
  //alert(JSON.stringify(sendingMessage));
  var temp = {}
  $.ajax({
    url: testUrl,
    dataType: 'json',
      type:method,
      data:JSON.stringify(sendingMessage),
      async: false,
      success: function(data, textStatus, jqXHR) {
    	temp = data;
      },
      error: function(data) {
        temp = data;
      }
  });
  //alert(JSON.stringify(temp));
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
    if (ob["accessSignature"] != undefined) {
    	accessSignature.push(ob["accessSignature"]);
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
    },1);
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
      "firstname":"foo",
      "lastname":"bar",
      "nickname":"foobar",
    },
    "expectedMessage":{
    },
    "expectedUnknownFields":["playerId","accessSignature"],
    "testTitle":"Inserting a new player",
  },
  ];
  setTimeout(function() {alert(playerId)},3000);

  //Waiting time parameter
  var waitTime = 0;
  var waitTimeInc = 5000;

  for (var i = 0; i < testCases.length; i ++) {
    var c = testCases[i];
    setTimeout(runTestCase(c), waitTime);
    //runTestCase(c);
    waitTime += waitTimeInc;
  }

  //Wait Until user is created
  setTimeout( function() {
	  //alert(getPlayerId(0));
  var testCasesDependent = [
  //Add the same player, should return an error
  {
    "method":"POST",
    "testUrl":"/players",
    "sendingMessage":{
      "email":"blablabla@gmail.com",
      "password":"foobar",
      "firstname":"foo",
      "lastname":"bar",
      "nickname":"foobar",
    },
    "expectedMessage":{
      "error": "EMAIL_EXISTS"
    },
    "expectedUnknownFields":[],
    "testTitle":"Inserting a new player with existing email",
  },
  //Login player
  //TODO get playerId from previous call
  {
    "method":"GET",
    "testUrl":"/players/"+getPlayerId(0)+"?password=foobar",
    "sendingMessage":{
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
    "testUrl":"/players/"+getPlayerId(0)+"?password=foobarla",
    "sendingMessage":{
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
    "testUrl":"/players/12345"+"?password=foobar",
    "sendingMessage":{
    },
    "expectedMessage":{
      "error": "WRONG_PLAYER_ID",
    },
    "expectedUnknownFields":[],
    "testTitle":"Login player with wrong id",
  },
  //Submit a game request
  //TODO: Obtain game Id from previous call
//  {
//    "method":"POST",
//    "testUrl":"/matcher",
//    "sendingMessage":{
//      "playerIds":getPlayerId(0),
//      "gameId":1,
//    },
//    "expectedMessage":{
//    },
//    "expectedUnknownFields":["matchId"],
//    "before": {
//      "method":"GET",
//      "testUrl":"/players/"+getPlayerId(0),
//      "sendingMessage":{
//        "password":"foobar",
//      },
//      "requiredKeys":["accessSignature"]
//    },
//    "testTitle":"Delete a player with wrong Id",
//  },
  //Delete a player Info with wrong accessSignature
  {
    "method":"DELETE",
    "testUrl":"/players/"+getPlayerId(0)+"?accessSignature=foobar",
    "sendingMessage":{
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
    "testUrl":"/players/12345"+"?accessSignature="+getAccessSignature(0),
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
  ];

  for (var i = 0; i < testCasesDependent.length; i ++) {
    var c = testCasesDependent[i];
    //wait between test cases
    //setTimeout is async
    setTimeout(runTestCase(c), waitTime);
    //runTestCase(c);
    waitTime += waitTimeInc*4;
  }
  }, 5000);
  setTimeout( function() {
  var testCasesTearDown = [
  //Deleting a player
  {
    "method":"DELETE",
    "testUrl":"/players/"+getPlayerId(0)+"?accessSignature="+getAccessSignature(1),
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
  }];
  for (var i = 0; i < testCasesTearDown.length; i ++) {
	    var c = testCasesTearDown[i];
	    //wait between test cases
	    //setTimeout is async
	    setTimeout(runTestCase(c), waitTime);
	    //runTestCase(c);
	    waitTime += waitTimeInc*4;
	  }
	  }, 10000);
  
}

$(document).ready(function() {
testPlayerV1();
});

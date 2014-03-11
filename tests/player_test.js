//Test under jQuery and QUnit
function testAjax(method, testUrl, sendingMessage, expectedMessage, expectedUnknownFields, before, testTitle) {
  if (before != undefined) {
    for (var i = 0; i < before.length; i ++){
      var beforeOb = before[i];
      var t = pureAjax(
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
        for (var i = 0; i < expectedUnknownFields.length; i ++) {
          var k = expectedUnknownFields[i];
          if (parseData[k] != undefined) {
            expectedMessage[k] = parseData[k];
          }
        }
        testEqual(parsedData, expectedMessage, testTitle+" : called by success case");
        temp = parsedData;
      },
      error: function(data) {
        parsedData = $.parseJSON(data);
        for (var i = 0; i < expectedUnknownFields.length; i ++) {
          var k = expectedUnknownFields[i];
          if (parseData[k] != undefined) {
            expectedMessage[k] = parseData[k];
          }
        }
        testEqual(parsedData, expectedMessage, testTitle+" : called by error case")
        temp = parsedData;
      }
  });
  return temp;
}
//AjaxCall for before/after testing
//requiredKeys is the array of field name that we need to acquire from this call;
//return an object with required information
function pureAjax(method, testUrl, sendingMessage, requiredKeys){
  var temp = {}
  $.ajax({
    url: connecttUrl,
    dataType: 'json',
      type:method,
      data:sendingMessage,
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

function testEqual(testing, expected, title) {
  test( title, function() {
        deepEqual( testing, expected, "We expect two objects are equal.");
    });
}

function joinObject(dst,src) {
  for (var k in src) {
    if (src[k] != undefined){
      dst[k] = src[k];
    }
  }
  return dst;
}

/*Example of test driver
  Test cases should clearup the environment after testing unless impossible.
  Otherwise tests might succeed at the first time then fail later.

  Some fields are expected but the value is unknown, use expectedUnknownFields to pass these fields

*/

function testPlayer() {
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
  //Login player
  //TODO get playerId from previous call
  {
    "method":"GET",
    "testUrl":"/players/1234",
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
    "testUrl":"/players/1234",
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
  //Update a player Info
  {
    "method":"PUT",
    "testUrl":"/players/1234",
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
      "testUrl":"/players/1234",
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
    "testUrl":"/players/1234",
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
      "testUrl":"/players/1234",
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
    "testUrl":"/players/1234",
    "sendingMessage":{},
    "expectedMessage":{
      "firstName":"foo1",
      "lastName":"bar1",
      "nickName":"foobar1",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/1234",
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
      "testUrl":"/players/1234",
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Get player Info with wrong Id",
  },
  //Deleting a player
  {
    "method":"DELETE",
    "testUrl":"/players/1234",
    "sendingMessage":{
    },
    "expectedMessage":{
      "success": "DELETED_PLAYER",
    },
    "expectedUnknownFields":[],
    "before": {
      "method":"GET",
      "testUrl":"/players/1234",
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Delete a player",
  },
  //Update a player Info with wrong accessSignature
  {
    "method":"DELETE",
    "testUrl":"/players/1234",
    "sendingMessage":{
      "accessSignature":"foobarwrongsignature",
    },
    "expectedMessage":{
      "error": "WRONG_ACCESS_SIGNATURE",
    },
    "expectedUnknownFields":[],
    "testTitle":"Delete a player with wrong accessSignature",
  },
  //Update a player Info with wrong Id
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
      "testUrl":"/players/1234",
      "sendingMessage":{
        "password":"foobar",
      },
      "requiredKeys":["accessSignature"]
    },
    "testTitle":"Delete a player with wrong Id",
  },
  ];

  //static URL definition
  var StaticURL = ""; // TODO: Change later

  for (var i = 0; i < testCases.length; i ++) {
    var c = testCases[i];
    testAjax(c["method"],
      StaticURL+c["testUrl"],
      c["sendingMessage"],
      c["expectedMessage"],
      c["expectedUnknownFields"],
      c["before"],
      c["testTitle"]);
  }
}
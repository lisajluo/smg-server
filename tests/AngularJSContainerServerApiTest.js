var domainUrl = "http://6.smg-server.appspot.com";

var validUserId1 = "5508129932443648";
var validUserPassword1 = "111111";
var validUserSignature1 = "4a25973b52f1dfc5c73c4334ec0f1185";
var validUserEmail1 = "serverTest1@angularJS_container.com";
var validUserFirstName1 = "test";
var validUserLastName1 = "server";
var validUserNickName1 = "001";

var validUserId2 = "6243981713211392";
var validUserPassword2 = "111111";
var validUserSignature2 = "67d38f4a69041502b14c253f564b19f9";
var validUserEmail2 = "serverTest2@angularJS_container.com";
var validUserFirstName2 = "test";
var validUserLastName2 = "server";
var validUserNickName2 = "002";

var invalidUserId = "invalid";
var invalidUserPassword = "invalid";

var validGameId = "5731972085186560";
var invalidGameId = "000";
var validGameUrl = "http://banqi-smg.appspot.com/";

var insertMatchData = {
	accessSignature: validUserSignature1,
	playerIds: [validUserId1, validUserId2],
	gameId: validGameId
};

var playerInitialMove = {
	accessSignature: validUserSignature1,
	playerIds: [validUserId1, validUserId2],
	operations:[
		{
			type: "SetTurn",
			playerId: validUserId1,
			numberOfSecondsForTurn: 0
		},
		{
			type: "Set",
			key: "C0",
			value: "rgen",
			visibleToPlayerIds: "ALL"
		},
		{
			type: "SetVisibility",
			key: "C0",
			visibleToPlayerIds: []
		}
	]
};

var validPlayerEngGameMove = {
	accessSignature: validUserSignature1,
	playerIds: [validUserId1, validUserId2],
	operations:[
		{
			type: "SetTurn",
			playerId: validUserId1,
			numberOfSecondsForTurn: 0
		},
		{
			type: "EndGame",
			playerIdToScore: {validUserId1: 1}
		}
	],
	gameOverReason: "Over"
};

var invalidPlayerEngGameMoveWithNoGameOverReason = {
	accessSignature: validUserSignature1,
	playerIds: [validUserId1, validUserId2],
	operations:[
		{
			type: "SetTurn",
			playerId: validUserId1,
			numberOfSecondsForTurn: 0
		},
		{
			type: "EndGame",
			playerIdToScore: {validUserId1: 1}
		}
	]
};

/*
 * Get user self info
 */
asyncTest("Get user self info", function(){
	var xhr;
	if (window.XMLHttpRequest){
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xhr=new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		xhr=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xhr.open("GET", domainUrl + "/playerInfo?playerId=" + validUserId1 + "&targetId=" + validUserId1 + "&accessSignature=" + validUserSignature1, true);
	xhr.responseType = 'json';
	xhr.onreadystatechange = function(){
		var status;
		var data;
		if(xhr.readyState == 4){
			status = xhr.status;
			if (status == 200){
				data = JSON.parse(JSON.stringify(xhr.response));
				ok(data["email"], validUserEmail1);
				ok(data["firstname"], validUserFirstName1);
				ok(data["lastname"], validUserLastName1);
				ok(data["nickname"], validUserNickName1);
				start();
			} else {}
		}
	};
	xhr.send();
});

/*
 * Get another user info
 */
asyncTest("Get another user info", function(){
	var xhr;
	if (window.XMLHttpRequest){
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xhr=new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		xhr=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xhr.open("GET", domainUrl + "/playerInfo?playerId=" + validUserId1 + "&targetId=" + validUserId2 + "&accessSignature=" + validUserSignature1, true);
	xhr.responseType = 'json';
	xhr.onreadystatechange = function(){
		var status;
		var data;
		if(xhr.readyState == 4){
			status = xhr.status;
			if (status == 200){
				data = JSON.parse(JSON.stringify(xhr.response));
				ok(data["firstname"], validUserFirstName2);
				ok(data["nickname"], validUserNickName2);
				start();
			} else {}
		}
	};
	xhr.send();
});

/*
 * Insert a new match
 */
asyncTest("Insert a new match", function(){
	var xhr;
	if (window.XMLHttpRequest){
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xhr=new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		xhr=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xhr.open("POST", domainUrl + "/newMatch", true);
	xhr.responseType = 'json';
	xhr.onreadystatechange = function(){
		var status;
		var data;
		if(xhr.readyState == 4){
			status = xhr.status;
			if (status == 200){
				data = JSON.parse(JSON.stringify(xhr.response));
				ok(data["playerIds"], true);
				ok(data["matchId"], true);
				start();

				/*
				 * Get a game's info
				 */
				asyncTest("Get a game's info", function(){
					var xhr;
					if (window.XMLHttpRequest){
						// code for IE7+, Firefox, Chrome, Opera, Safari
						xhr=new XMLHttpRequest();
					} else {
						// code for IE6, IE5
						xhr=new ActiveXObject("Microsoft.XMLHTTP");
					}
					xhr.open("GET", domainUrl + "/games/" + validGameId, true);
					xhr.responseType = 'json';
					xhr.onreadystatechange = function(){
						var status;
						var data;
						if(xhr.readyState == 4){
							status = xhr.status;
							if (status == 200){
								data = JSON.parse(JSON.stringify(xhr.response));
								ok(data["url"], validGameUrl);
								start();
							} else {}
						}
					};
					xhr.send();
				});

				/*
				 * Get a game's info with invalid gameId
				 */
				asyncTest("Get a game's info with invalid gameId", function(){
					var xhr;
					if (window.XMLHttpRequest){
						// code for IE7+, Firefox, Chrome, Opera, Safari
						xhr=new XMLHttpRequest();
					} else {
						// code for IE6, IE5
						xhr=new ActiveXObject("Microsoft.XMLHTTP");
					}
					xhr.open("GET", domainUrl + "/games/" + invalidGameId, true);
					xhr.responseType = 'json';
					xhr.onreadystatechange = function(){
						var status;
						var data;
						if(xhr.readyState == 4){
							status = xhr.status;
							if (status == 200){
								data = JSON.parse(JSON.stringify(xhr.response));
								ok(data["error"], "WRONG_GAME_ID");
								start();
							} else {}
						}
					};
					xhr.send();
				});

				/*
				 * Get match info
				 */
				asyncTest("Get match info", function(){
					var xhr;
					if (window.XMLHttpRequest){
						// code for IE7+, Firefox, Chrome, Opera, Safari
						xhr=new XMLHttpRequest();
					} else {
						// code for IE6, IE5
						xhr=new ActiveXObject("Microsoft.XMLHTTP");
					}
					xhr.open("GET", domainUrl + "/newMatch/" + validUserId1 + "?accessSignature=" + validUserSignature1 + "&gameId=" + validGameId, true);
					xhr.responseType = 'json';
					xhr.onreadystatechange = function(){
						var status;
						var data;
						if(xhr.readyState == 4){
							status = xhr.status;
							if (status == 200){
								data = JSON.parse(JSON.stringify(xhr.response));
								ok(data.hasOwnProperty("matchId"), true);
								var validMatchId = data["matchId"];
								console.log(validMatchId);
								start();

								/*
								 * Get match state for player 1
								 */
								asyncTest("Get match state for player 1", function(){
									var xhr;
									if (window.XMLHttpRequest){
										// code for IE7+, Firefox, Chrome, Opera, Safari
										xhr=new XMLHttpRequest();
									} else {
										// code for IE6, IE5
										xhr=new ActiveXObject("Microsoft.XMLHTTP");
									}
									xhr.open("GET", domainUrl + "/state/" + validMatchId + "?playerId=" + validUserId1 + "&accessSignature=" + validUserSignature1, true);
									xhr.responseType = 'json';
									xhr.onreadystatechange = function(){
										var status;
										var data;
										if(xhr.readyState == 4){
											status = xhr.status;
											if (status == 200){
												data = JSON.parse(JSON.stringify(xhr.response));
												ok(data.hasOwnProperty("playerThatHasLastTurn"), true);
												ok(data.hasOwnProperty("matchId"), true);
												ok(data.hasOwnProperty("state"), true);
												ok(data.hasOwnProperty("lastMove"), true);
												start();
											} else {}
										}
									};
									xhr.send();
								});

								/*
								 * Get match state for player 2
								 */
								asyncTest("Get match state for player 2", function(){
									var xhr;
									if (window.XMLHttpRequest){
										// code for IE7+, Firefox, Chrome, Opera, Safari
										xhr=new XMLHttpRequest();
									} else {
										// code for IE6, IE5
										xhr=new ActiveXObject("Microsoft.XMLHTTP");
									}
									xhr.open("GET", domainUrl + "/state/" + validMatchId + "?playerId=" + validUserId2 + "&accessSignature=" + validUserSignature2, true);
									xhr.responseType = 'json';
									xhr.onreadystatechange = function(){
										var status;
										var data;
										if(xhr.readyState == 4){
											status = xhr.status;
											if (status == 200){
												data = JSON.parse(JSON.stringify(xhr.response));
												ok(data.hasOwnProperty("playerThatHasLastTurn"), true);
												ok(data.hasOwnProperty("matchId"), true);
												ok(data.hasOwnProperty("state"), true);
												ok(data.hasOwnProperty("lastMove"), true);
												start();
											} else {}
										}
									};
									xhr.send();
								});

								/*
								 * Send the player's move to the server.
								 */
								asyncTest("Send the player's move to the server", function(){
									var xhr;
									if (window.XMLHttpRequest){
										// code for IE7+, Firefox, Chrome, Opera, Safari
										xhr=new XMLHttpRequest();
									} else {
										// code for IE6, IE5
										xhr=new ActiveXObject("Microsoft.XMLHTTP");
									}
									xhr.open("POST", domainUrl + "/matches/" + validMatchId, true);
									xhr.responseType = 'json';
									xhr.onreadystatechange = function(){
										var status;
										var data;
										if(xhr.readyState == 4){
											status = xhr.status;
											if (status == 200){
												data = JSON.parse(JSON.stringify(xhr.response));
												ok(data.hasOwnProperty("playerThatHasLastTurn"), true);
												ok(data.hasOwnProperty("matchId"), true);
												ok(data.hasOwnProperty("state"), true);
												ok(data.hasOwnProperty("lastMove"), true);
												start();
											} else {}
										}
									};
									xhr.setRequestHeader("Content-type", "application/json");
									xhr.send(JSON.stringify(playerInitialMove));
									console.log(JSON.stringify(playerInitialMove));
								});

								/*
								 * Get new match state after initial move
								 */
								asyncTest("Get new match state after initial move", function(){
									var xhr;
									if (window.XMLHttpRequest){
										// code for IE7+, Firefox, Chrome, Opera, Safari
										xhr=new XMLHttpRequest();
									} else {
										// code for IE6, IE5
										xhr=new ActiveXObject("Microsoft.XMLHTTP");
									}
									xhr.open("GET", domainUrl + "/state/" + validMatchId + "?playerId=" + validUserId1 + "&accessSignature=" + validUserSignature1, true);
									xhr.responseType = 'json';
									xhr.onreadystatechange = function(){
										var status;
										var data;
										if(xhr.readyState == 4){
											status = xhr.status;
											if (status == 200){
												data = JSON.parse(JSON.stringify(xhr.response));
												ok(data.hasOwnProperty("playerThatHasLastTurn"), true);
												ok(data.hasOwnProperty("matchId"), true);
												ok(data.hasOwnProperty("state"), true);
												ok(data.hasOwnProperty("lastMove"), true);
												start();
											} else {}
										}
									};
									xhr.send();
								});

								/*
								 * Send invalid end game (missing end game reason) to the server.
								 */
								asyncTest("Send invalid end game (missing end game reason) to the server", function(){
									var xhr;
									if (window.XMLHttpRequest){
										// code for IE7+, Firefox, Chrome, Opera, Safari
										xhr=new XMLHttpRequest();
									} else {
										// code for IE6, IE5
										xhr=new ActiveXObject("Microsoft.XMLHTTP");
									}
									xhr.open("POST", domainUrl + "/matches/" + validMatchId, true);
									xhr.responseType = 'json';
									xhr.onreadystatechange = function(){
										var status;
										var data;
										if(xhr.readyState == 4){
											status = xhr.status;
											if (status == 200){
												data = JSON.parse(JSON.stringify(xhr.response));
												ok(data["error"], "MISSING_INFO");
												start();
											} else {}
										}
									};
									xhr.setRequestHeader("Content-type", "application/json");
									xhr.send(JSON.stringify(invalidPlayerEngGameMoveWithNoGameOverReason));
								});

								/*
								 * Send end game to the server.
								 */
								asyncTest("Send end game to the server", function(){
									var xhr;
									if (window.XMLHttpRequest){
										// code for IE7+, Firefox, Chrome, Opera, Safari
										xhr=new XMLHttpRequest();
									} else {
										// code for IE6, IE5
										xhr=new ActiveXObject("Microsoft.XMLHTTP");
									}
									xhr.open("POST", domainUrl + "/matches/" + validMatchId, true);
									xhr.responseType = 'json';
									xhr.onreadystatechange = function(){
										var status;
										var data;
										if(xhr.readyState == 4){
											status = xhr.status;
											if (status == 200){
												data = JSON.parse(JSON.stringify(xhr.response));
												ok(data.hasOwnProperty("playerThatHasLastTurn"), true);
												ok(data.hasOwnProperty("matchId"), true);
												ok(data.hasOwnProperty("state"), true);
												ok(data.hasOwnProperty("lastMove"), true);
												start();
											} else {}
										}
									};
									xhr.setRequestHeader("Content-type", "application/json");
									xhr.send(JSON.stringify(validPlayerEngGameMove));
								});

							} else {}
						}
					};
					xhr.send();
				});
			} else {}
		}
	};
	xhr.setRequestHeader("Content-type", "application/json");
	xhr.send(JSON.stringify(insertMatchData));
});






//
///*
// * Receive the game over score and reason from the server
// * Should return gameOverScores and gameOverReason
// */
//test("Receive the game over score and reason from the server", function(){
//	var xhr;
//	if (window.XMLHttpRequest){
//		// code for IE7+, Firefox, Chrome, Opera, Safari
//		xhr=new XMLHttpRequest();
//	} else {
//		// code for IE6, IE5
//		xhr=new ActiveXObject("Microsoft.XMLHTTP");
//	}
//	xhr.open("GET", matchesUrl + "matchId?gameOverScores&gameOverReason", true);
//	xhr.responseType = 'json';
//	xhr.onreadystatechange = function(){
//		var status;
//		var data;
//		if(xhr.readyState == 4){
//			status = xhr.status;
//			if (status == 200){
//				data = xhr.response;
//				ok(data.has(gameOverScores));
//				ok(data.has(gameOverReason));
//			} else {}
//		}
//	};
//	xhr.send();
//});
//
///*
// * Get a player's infomation from the server
// * Should return playerId, name, nickName, pic, tokens
// */
//test("Get a player's infomation from the server", function(){
//	var xhr;
//	if (window.XMLHttpRequest){
//		// code for IE7+, Firefox, Chrome, Opera, Safari
//		xhr=new XMLHttpRequest();
//	} else {
//		// code for IE6, IE5
//		xhr=new ActiveXObject("Microsoft.XMLHTTP");
//	}
//	xhr.open("GET", playersUrl + "playerId={playerId}", true);
//	xhr.responseType = 'json';
//	xhr.onreadystatechange = function(){
//		var status;
//		var data;
//		if(xhr.readyState == 4){
//			status = xhr.status;
//			if (status == 200){
//				data = xhr.response;
//				ok(data.has(playerId));
//				ok(data.has(name));
//				ok(data.has(nickName));
//				ok(data.has(pic));
//				ok(data.has(tokens));
//			} else {}
//		}
//	};
//	xhr.send();
//});
//
///*
// * Register a new account
// * Should return playerId
// */
//test("Register a new account", function(){
//	var xhr;
//	if (window.XMLHttpRequest){
//		// code for IE7+, Firefox, Chrome, Opera, Safari
//		xhr=new XMLHttpRequest();
//	} else {
//		// code for IE6, IE5
//		xhr=new ActiveXObject("Microsoft.XMLHTTP");
//	}
//	xhr.open("POST", playersUrl, true);
//	xhr.responseType = 'json';
//	xhr.onreadystatechange = function(){
//		var status;
//		var data;
//		if(xhr.readyState == 4){
//			status = xhr.status;
//			if (status == 200){
//				data = xhr.response;
//				ok(data.has(playerId));
//			} else {}
//		}
//	};
//	xhr.setRequestHeader("Content-type", "application/json");
//	xhr.send(registerInfo);
//});
//
///*
// * Register a new account with exists email
// * Should return {"error": "EMAIL_EXISTS"}
// */
//test("Register a new account with exists email", function(){
//	var xhr;
//	if (window.XMLHttpRequest){
//		// code for IE7+, Firefox, Chrome, Opera, Safari
//		xhr=new XMLHttpRequest();
//	} else {
//		// code for IE6, IE5
//		xhr=new ActiveXObject("Microsoft.XMLHTTP");
//	}
//	xhr.open("POST", playersUrl, true);
//	xhr.responseType = 'json';
//	xhr.onreadystatechange = function(){
//		var status;
//		var data;
//		if(xhr.readyState == 4){
//			status = xhr.status;
//			if (status == 200){
//				data = xhr.response;
//				deepEqual(data, {"error": "EMAIL_EXISTS"});
//			} else {}
//		}
//	};
//	xhr.setRequestHeader("Content-type", "application/json");
//	xhr.send(registerInfo);
//});
//

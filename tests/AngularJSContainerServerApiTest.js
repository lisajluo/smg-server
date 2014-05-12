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
var invalidUserSignature = "invalid";

var validGameId = "5731972085186560";
var invalidGameId = "000";
var validGameUrl = "http://banqi-smg.appspot.com/";

var invalidMatchId = "invalid";

var validInsertMatchData = {
	accessSignature: validUserSignature1,
	playerIds: [validUserId1, validUserId2],
	gameId: validGameId
};

var invalidInsertMatchDataWithInvalidAccessSignature = {
	accessSignature: invalidUserSignature,
	playerIds: [validUserId1, validUserId2],
	gameId: validGameId
};

var invalidInsertMatchDataWithInvalidUserId = {
	accessSignature: validUserSignature1,
	playerIds: [invalidUserId, validUserId2],
	gameId: validGameId
};

var invalidInsertMatchDataWithInvalidGameId = {
	accessSignature: validUserSignature1,
	playerIds: [validUserId1, validUserId2],
	gameId: invalidGameId
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

var playerInitialMoveWithWrongUserId = {
	accessSignature: validUserSignature1,
	playerIds: [invalidUserId, validUserId2],
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

var playerInitialMoveWithWrongAccessSignature = {
	accessSignature: invalidUserSignature,
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
				equal(data["email"], validUserEmail1);
				equal(data["firstname"], validUserFirstName1);
				equal(data["lastname"], validUserLastName1);
				equal(data["nickname"], validUserNickName1);
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
				equal(data["firstname"], validUserFirstName2);
				equal(data["nickname"], validUserNickName2);
				start();
			} else {}
		}
	};
	xhr.send();
});

/*
 * Insert a new match with invalid userID
 */
asyncTest("Insert a new match with invalid userID", function(){
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
				equal(data["error"], "WRONG_PLAYER_ID");
				start();
			} else {}
		}
	};
	xhr.setRequestHeader("Content-type", "application/json");
	xhr.send(JSON.stringify(invalidInsertMatchDataWithInvalidUserId));
});

/*
 * Insert a new match with invalid accessSignature
 */
asyncTest("Insert a new match with invalid accessSignature", function(){
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
				equal(data["error"], "WRONG_ACCESS_SIGNATURE");
				start();
			} else {}
		}
	};
	xhr.setRequestHeader("Content-type", "application/json");
	xhr.send(JSON.stringify(invalidInsertMatchDataWithInvalidAccessSignature));
});

/*
 * Insert a new match with invalid gameId
 */
asyncTest("Insert a new match with invalid gameId", function(){
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
				equal(data["error"], "WRONG_GAME_ID");
				start();
			} else {}
		}
	};
	xhr.setRequestHeader("Content-type", "application/json");
	xhr.send(JSON.stringify(invalidInsertMatchDataWithInvalidGameId));
});

/*
 * Get match info when no match is found (will insert later...)
 */
asyncTest("Get match info when no match is found", function(){
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
				equal(data["error"], "NO_MATCH_FOUND");
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
				deepEqual(data["playerIds"], [validUserId1, validUserId2]);
				ok(data.hasOwnProperty("matchId"));
				start();

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
								equal(data["error"], "WRONG_GAME_ID");
								start();
							} else {}
						}
					};
					xhr.send();
				});

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
								equal(data["url"], validGameUrl);
								start();
							} else {}
						}
					};
					xhr.send();
				});

				/*
				 * Get match info with wrong user ID
				 */
				asyncTest("Get match info with wrong user ID", function(){
					var xhr;
					if (window.XMLHttpRequest){
						// code for IE7+, Firefox, Chrome, Opera, Safari
						xhr=new XMLHttpRequest();
					} else {
						// code for IE6, IE5
						xhr=new ActiveXObject("Microsoft.XMLHTTP");
					}
					xhr.open("GET", domainUrl + "/newMatch/" + invalidUserId + "?accessSignature=" + validUserSignature1 + "&gameId=" + validGameId, true);
					xhr.responseType = 'json';
					xhr.onreadystatechange = function(){
						var status;
						var data;
						if(xhr.readyState == 4){
							status = xhr.status;
							if (status == 200){
								data = JSON.parse(JSON.stringify(xhr.response));
								equal(data["error"], "WRONG_PLAYER_ID");
								start();
							} else {}
						}
					};
					xhr.send();
				});

				/*
				 * Get match info with wrong user accessSignature
				 */
				asyncTest("Get match info with wrong user accessSignature", function(){
					var xhr;
					if (window.XMLHttpRequest){
						// code for IE7+, Firefox, Chrome, Opera, Safari
						xhr=new XMLHttpRequest();
					} else {
						// code for IE6, IE5
						xhr=new ActiveXObject("Microsoft.XMLHTTP");
					}
					xhr.open("GET", domainUrl + "/newMatch/" + validUserId1 + "?accessSignature=" + invalidUserSignature + "&gameId=" + validGameId, true);
					xhr.responseType = 'json';
					xhr.onreadystatechange = function(){
						var status;
						var data;
						if(xhr.readyState == 4){
							status = xhr.status;
							if (status == 200){
								data = JSON.parse(JSON.stringify(xhr.response));
								equal(data["error"], "WRONG_ACCESS_SIGNATURE");
								start();
							} else {}
						}
					};
					xhr.send();
				});

				/*
				 * Get match info with wrong gameId
				 */
				asyncTest("Get match info with wrong gameId", function(){
					var xhr;
					if (window.XMLHttpRequest){
						// code for IE7+, Firefox, Chrome, Opera, Safari
						xhr=new XMLHttpRequest();
					} else {
						// code for IE6, IE5
						xhr=new ActiveXObject("Microsoft.XMLHTTP");
					}
					xhr.open("GET", domainUrl + "/newMatch/" + validUserId1 + "?accessSignature=" + validUserSignature1 + "&gameId=" + invalidGameId, true);
					xhr.responseType = 'json';
					xhr.onreadystatechange = function(){
						var status;
						var data;
						if(xhr.readyState == 4){
							status = xhr.status;
							if (status == 200){
								data = JSON.parse(JSON.stringify(xhr.response));
								equal(data["error"], "WRONG_GAME_ID");
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
								ok(data.hasOwnProperty("matchId"));
								var validMatchId = data["matchId"];
								start();

								/*
								 * Get match state for player 1 with wrong user ID
								 */
								asyncTest("Get match state for player 1 with wrong user ID", function(){
									var xhr;
									if (window.XMLHttpRequest){
										// code for IE7+, Firefox, Chrome, Opera, Safari
										xhr=new XMLHttpRequest();
									} else {
										// code for IE6, IE5
										xhr=new ActiveXObject("Microsoft.XMLHTTP");
									}
									xhr.open("GET", domainUrl + "/state/" + validMatchId + "?playerId=" + invalidUserId + "&accessSignature=" + validUserSignature1, true);
									xhr.responseType = 'json';
									xhr.onreadystatechange = function(){
										var status;
										var data;
										if(xhr.readyState == 4){
											status = xhr.status;
											if (status == 200){
												data = JSON.parse(JSON.stringify(xhr.response));
												equal(data["error"], "WRONG_PLAYER_ID");
												start();
											} else {}
										}
									};
									xhr.send();
								});

								/*
								 * Get match state for player 1 with wrong accessSignature
								 */
								asyncTest("Get match state for player 1 with wrong accessSignature", function(){
									var xhr;
									if (window.XMLHttpRequest){
										// code for IE7+, Firefox, Chrome, Opera, Safari
										xhr=new XMLHttpRequest();
									} else {
										// code for IE6, IE5
										xhr=new ActiveXObject("Microsoft.XMLHTTP");
									}
									xhr.open("GET", domainUrl + "/state/" + validMatchId + "?playerId=" + validUserId1 + "&accessSignature=" + invalidUserSignature, true);
									xhr.responseType = 'json';
									xhr.onreadystatechange = function(){
										var status;
										var data;
										if(xhr.readyState == 4){
											status = xhr.status;
											if (status == 200){
												data = JSON.parse(JSON.stringify(xhr.response));
												equal(data["error"], "WRONG_ACCESS_SIGNATURE");
												start();
											} else {}
										}
									};
									xhr.send();
								});

								/*
								 * Get match state for player 1 with wrong match Id
								 */
								asyncTest("Get match state for player 1 with wrong match Id", function(){
									var xhr;
									if (window.XMLHttpRequest){
										// code for IE7+, Firefox, Chrome, Opera, Safari
										xhr=new XMLHttpRequest();
									} else {
										// code for IE6, IE5
										xhr=new ActiveXObject("Microsoft.XMLHTTP");
									}
									xhr.open("GET", domainUrl + "/state/" + invalidMatchId + "?playerId=" + validUserId1 + "&accessSignature=" + validUserSignature1, true);
									xhr.responseType = 'json';
									xhr.onreadystatechange = function(){
										var status;
										var data;
										if(xhr.readyState == 4){
											status = xhr.status;
											if (status == 200){
												data = JSON.parse(JSON.stringify(xhr.response));
												equal(data["error"], "WRONG_MATCH_ID");
												start();
											} else {}
										}
									};
									xhr.send();
								});

								/*
								 * Get match state for player 1 with missing info
								 */
								asyncTest("Get match state for player 1 with missing info", function(){
									var xhr;
									if (window.XMLHttpRequest){
										// code for IE7+, Firefox, Chrome, Opera, Safari
										xhr=new XMLHttpRequest();
									} else {
										// code for IE6, IE5
										xhr=new ActiveXObject("Microsoft.XMLHTTP");
									}
									xhr.open("GET", domainUrl + "/state/?playerId=" + validUserId1 + "&accessSignature=" + validUserSignature1, true);
									xhr.responseType = 'json';
									xhr.onreadystatechange = function(){
										var status;
										var data;
										if(xhr.readyState == 4){
											status = xhr.status;
											if (status == 200){
												data = JSON.parse(JSON.stringify(xhr.response));
												equal(data["error"], "MISSING_INFO");
												start();
											} else {}
										}
									};
									xhr.send();
								});



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
												ok(data.hasOwnProperty("playerThatHasLastTurn"));
												ok(data.hasOwnProperty("matchId"));
												ok(data.hasOwnProperty("state"));
												ok(data.hasOwnProperty("lastMove"));
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
												ok(data.hasOwnProperty("playerThatHasLastTurn"));
												ok(data.hasOwnProperty("matchId"));
												ok(data.hasOwnProperty("state"));
												ok(data.hasOwnProperty("lastMove"));
												start();
											} else {}
										}
									};
									xhr.send();
								});

								/*
								 * Send the player's move to the server with wrong user ID.
								 */
								asyncTest("Send the player's move to the server with wrong user ID", function(){
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
												equal(data["error"], "WRONG_PLAYER_ID");
												start();
											} else {}
										}
									};
									xhr.setRequestHeader("Content-type", "application/json");
									xhr.send(JSON.stringify(playerInitialMoveWithWrongUserId));
								});

								/*
								 * Send the player's move to the server with wrong accessSignature.
								 */
								asyncTest("Send the player's move to the server with wrong accessSignature", function(){
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
												equal(data["error"], "WRONG_ACCESS_SIGNATURE");
												start();
											} else {}
										}
									};
									xhr.setRequestHeader("Content-type", "application/json");
									xhr.send(JSON.stringify(playerInitialMoveWithWrongAccessSignature));
								});

								/*
								 * Send the player's move to the server without convert the move to JSON.
								 */
								asyncTest("Send the player's move to the server without convert the move to JSON", function(){
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
												equal(data["error"], "JSON_PARSE_ERROR");
												start();
											} else {}
										}
									};
									xhr.setRequestHeader("Content-type", "application/json");
									xhr.send(playerInitialMove);
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
												ok(data.hasOwnProperty("playerThatHasLastTurn"));
												ok(data.hasOwnProperty("matchId"));
												ok(data.hasOwnProperty("state"));
												ok(data.hasOwnProperty("lastMove"));
												start();
											} else {}
										}
									};
									xhr.setRequestHeader("Content-type", "application/json");
									xhr.send(JSON.stringify(playerInitialMove));
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
												ok(data.hasOwnProperty("playerThatHasLastTurn"));
												ok(data.hasOwnProperty("matchId"));
												ok(data.hasOwnProperty("state"));
												ok(data.hasOwnProperty("lastMove"));
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
												equal(data["error"], "MISSING_INFO");
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
												ok(data.hasOwnProperty("playerThatHasLastTurn"));
												ok(data.hasOwnProperty("matchId"));
												ok(data.hasOwnProperty("state"));
												ok(data.hasOwnProperty("lastMove"));
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
	xhr.send(JSON.stringify(validInsertMatchData));
});
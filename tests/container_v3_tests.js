PLAYER_ID = 1042
ACCESS_SIGNATURE = "hD7d7DDdjsh12WQ"

// Save current game(same game state????)
$.ajax({
    url: “http: //1.smg-server.appspot.com/save”,
    dataType: 'json',
    type: 'POST',
    data: {
        'accessSignature': ACCESS_SIGNATURE,
        "type": "save_game_request"
        "playerid": PLAYER_ID
    },
    success: function (actural) {
        var expectedData = {}
        assert.deepEqual(actural, expected);
    },
    error: function (jqXHR, textStatus, errorThrown) {
        alert('error ' + textStatus + " " + errorThrown);
    }
});
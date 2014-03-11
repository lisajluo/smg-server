// Save current game(same game state????)
function testSaveGameState(successCallBack){
    $.ajax({
        url: "http: //3.smg-server.appspot.com/save",
        dataType: 'json',
        type: 'POST',
        data: {
            'accessSignature': "hD7d7DDdjsh12WQ",
            "type": "save_game_request",
            "playerid": 42
        },
        success: successCallBack,
        error: function (jqXHR, textStatus, errorThrown) {
            alert('error' + textStatus + " " + errorThrown);
        }
    });
}

test("Save Game State", function(){
    stop(1000);     
    testSaveGameState(function(actual) {
        var expected = {
            "gameState": {
                "state_1":"state_1",
                "state_2":"state_2"
            }
        };
        deepEqual(expected, actual, "Expected data does not match actual data.");
        start();
    })
})
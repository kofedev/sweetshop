function goToCart() {
    const ALERT_MESSAGE = "Las cookies no están habilitadas en su navegador";
    // first call: detect and generate cookie if it needs
    const urlToControllerDetectCookieCall = "/cookie";
    const urlToControllerCheckCookieCall = "/checkcookie";
    $.ajax({
        url: urlToControllerDetectCookieCall,
        contentType: "application/json",
        type: 'GET',
        success: function (receivedData) {
            // second checking of cookies
            $.ajax({
                url: urlToControllerCheckCookieCall,
                contentType: "application/json",
                type: 'GET',
                success: function () {
                    // main call
                    const urlToCartController = "/cart";
                    window.location.assign(urlToCartController);
                },
                statusCode: {
                    409: function () {
                        alert(ALERT_MESSAGE);
                    }
                }
            });
        }
    });
}

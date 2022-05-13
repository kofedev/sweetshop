displayQuantityItemsInCart();
function displayQuantityItemsInCart() {
    let elementQuantityItemsInCart = document.getElementById("quantityItemsInCart");
    const ALERT_MESSAGE = "Vaya, algo ha pasado...";
    const urlToControllerGetQuantity = "/cartvalue";
    $.ajax({
        url: urlToControllerGetQuantity,
        contentType: "application/json",
        type: 'GET',
        success: function (receivedData) {
            elementQuantityItemsInCart.textContent = receivedData;
        },
        statusCode: {
            409: function () {
                console.log("ALERT!");
                alert(ALERT_MESSAGE);
            }
        }
    });
}
function displayZeroQuantityItemsInCart() {
    let elementQuantityItemsInCart = document.getElementById("quantityItemsInCart");
    elementQuantityItemsInCart.textContent = "0";
}
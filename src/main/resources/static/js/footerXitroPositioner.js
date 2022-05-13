//ToDo debouncing
function footerXitroPositioner() {
    doPosition();
}
function doPosition() {
    setSafeSpace();
    const displaySpaceElement = document.getElementById("displaySpace");
    const windowHeight = window.innerHeight;
    //const bodyHeight = document.body.clientHeight;
    const mainContentElement = document.getElementById("mainContent");
    const footerElement = document.getElementById("footer");
    if (document.body.clientHeight >= windowHeight) {
        displaySpaceElement.style.marginTop = "0px";
    }
    if ( mainContentElement.clientHeight + 500 < windowHeight) {
        displaySpaceElement.style.marginTop =
            (windowHeight - mainContentElement.clientHeight - footerElement.clientHeight - 550) + "px";
    }
}
function setSafeSpace () {
    const safeSpaceElement = document.getElementById("safeSpace");
    if (document.body.clientHeight - safeSpaceElement.clientHeight - 100 >= window.innerHeight) {
        safeSpaceElement.style.marginTop = "0px";
    } else {
        safeSpaceElement.style.marginTop = "320px";
    }
}
document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".confirm-action").forEach(function (button) {
        button.addEventListener("click", function (event) {
            const message = button.getAttribute("data-message") || "Are you sure?";
            if (!window.confirm(message)) {
                event.preventDefault();
            }
        });
    });

    const productType = document.getElementById("productTypeSelect");
    const expiryWrap = document.getElementById("expiryDateWrapper");

    function toggleExpiry() {
        if (!productType || !expiryWrap) {
            return;
        }
        expiryWrap.style.display = productType.value === "PERISHABLE" ? "block" : "none";
    }

    if (productType && expiryWrap) {
        toggleExpiry();
        productType.addEventListener("change", toggleExpiry);
    }

    const paymentType = document.getElementById("paymentTypeSelect");
    const refWrap = document.getElementById("paymentReferenceWrapper");

    function togglePaymentRef() {
        if (!paymentType || !refWrap) {
            return;
        }
        refWrap.style.display = paymentType.value === "ONLINE" ? "block" : "none";
    }

    if (paymentType && refWrap) {
        togglePaymentRef();
        paymentType.addEventListener("change", togglePaymentRef);
    }
});

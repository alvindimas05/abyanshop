const url = 'http://ancritbat.my.id:8880/api/',
url_img = url + "../images/";

window.alert = async (message) => await Swal.fire({
    title: message,
    icon: 'warning',
    confirmButtonColor: '#3085d6',
    confirmButtonText: 'OK',
});
window.confirm = async (title, message) => await Swal.fire({
    title: title,
    text: message,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Ya',
    cancelButtonText: 'Tidak'
});
$(document).ready(() => {
    let isAdmin = $.cookie("admin_id") != undefined;
    if ($.cookie("user_id") != undefined || isAdmin){
        $(".button-login").css("display","none");
        if(!isAdmin)
            $("img[alt='profil']").removeAttr("hidden").click(() => window.location.href = "/user.html");
        $(".button-logout").removeAttr("hidden").click(() => {
            $.removeCookie("user_id");
            $.removeCookie("admin_id");
            location.reload();
        });
    }
});
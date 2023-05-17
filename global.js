const url = 'https://ancritbat.my.id/api/',
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

        if(isAdmin){
            $("#nav-list").html("");
            $("#nav-list").append(`
            <li>
              <a href="/" class="block py-2 pl-3 pr-4 text-white bg-orange-700 rounded md:bg-transparent md:text-orange-700 md:p-0 md:text-lg" aria-current="page">Home</a>
            </li>
            <li>
              <a href="/tipe.html" class="block py-2 pl-3 pr-4 text-white bg-orange-700 rounded md:bg-transparent md:text-orange-700 md:p-0 md:text-lg" aria-current="page">Tipe</a>
            </li>
            <li>
              <a href="/produk.html" class="block py-2 pl-3 pr-4 text-white bg-orange-700 rounded md:bg-transparent md:text-orange-700 md:p-0 md:text-lg" aria-current="page">Produk</a>
            </li>
            `);
        } else $("img[alt='profil']").removeAttr("hidden").click(() => window.location.href = "/user.html");
        $(".button-logout").removeAttr("hidden").click(() => {
            $.removeCookie("user_id");
            $.removeCookie("admin_id");
            location.reload();
        });
    }
    $('[data-collapse-toggle="navbar-search"]').css("display", "none");
});
$( document ).ready(function() {
    // Shift nav in mobile when clicking the menu.
    $(document).on('click', "[data-toggle='wy-nav-top']", function() {
      $("[data-toggle='wy-nav-shift']").toggleClass("shift");
      $("[data-toggle='rst-versions']").toggleClass("shift");
    });
    // Close menu when you click a link.
    $(document).on('click', ".wy-menu-vertical .current ul li a", function() {
      $("[data-toggle='wy-nav-shift']").removeClass("shift");
      $("[data-toggle='rst-versions']").toggleClass("shift");
    });
    $(document).on('click', "[data-toggle='rst-current-version']", function() {
      $("[data-toggle='rst-versions']").toggleClass("shift-up");
    });
    // Make tables responsive
    $("table.docutils:not(.field-list)").wrap("<div class='wy-table-responsive'></div>");

    // ribbon drawer
    $("#ribbon .base").click(function(evt) {
        var ribbon = $('#ribbon');
        if (ribbon.height() == 40) {
            ribbon.animate({height: "290px"}, 500);
            $("#disclaimer").show();
            $("#ribbon .base i").removeClass("fa-chevron-down")
                                    .addClass("fa-chevron-up");
        } else {
            ribbon.animate({height: "40px"}, 500);
            $("#disclaimer").fadeOut("slow");
            $("#ribbon .base i").removeClass("fa-chevron-up")
                                .addClass("fa-chevron-down");
        }
        evt.stopPropagation();
    });

    // fix anchor tag offsets due to fixed header
    var header_height = $('#header').height();

    // Case 1 - same page anchor
    $("[href^='#']").not("[href~='#']").mouseup(function(evt){
        setTimeout(function(){
            window.scrollBy(0, -header_height);
        }, 100);
    });
    // Case 2 - different page anchor
    var hash = window.location.hash;
    if (hash) {
        setTimeout(function(){
            window.scrollBy(0, -header_height);
        }, 100);
    }


});

window.SphinxRtdTheme = (function (jquery) {
    var stickyNav = (function () {
        var navBar,
            win,
            stickyNavCssClass = 'stickynav',
            applyStickNav = function () {
                if (navBar.height() <= win.height()) {
                    navBar.addClass(stickyNavCssClass);
                } else {
                    navBar.removeClass(stickyNavCssClass);
                }
            },
            enable = function () {
                applyStickNav();
                win.on('resize', applyStickNav);
            },
            init = function () {
                navBar = jquery('nav.wy-nav-side:first');
                win    = jquery(window);
            };
        jquery(init);
        return {
            enable : enable
        };
    }());
    return {
        StickyNav : stickyNav
    };
}($));

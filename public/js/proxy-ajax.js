function proxifyUrl(url) {
    return "/secure-chat/get-html-from-url/" + encodeURIComponent(url);
}

function unproxifyUrl(url) {
    return decodeURIComponent(url.split("/secure-chat/get-html-from-url/")[1]);
}

/*var jqAjax = $.ajax;
$.ajax = function(settings) {
    if (settings && settings.url) {
        settings.url = proxifyUrl(settings.url);

        var settingsCopy = {
            url: unproxifyUrl(settings.url),
            complete: settings.complete,
            success: settings.success,
            error: settings.error
        };

        settings.complete = function(jqXHR, textStatus) {
            settingsCopy.complete(jqXHR, textStatus);
        };

        settings.success = function(data, textStatus, jqXHR) {
            settingsCopy.success(data, textStatus, jqXHR);
        };

        settings.error = function(jqXHR, textStatus, errorThrown) {
            settingsCopy.error(jqXHR, textStatus, errorThrown);
        };
    }
    jqAjax(settings);
};*/
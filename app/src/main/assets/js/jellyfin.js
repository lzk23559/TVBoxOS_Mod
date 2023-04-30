(function () {
    this.serverurl = '';
    this.userid = '';
    this.accesstoken = '';

    this.init = function (ext) {
        console.log("ext:", ext);
        if (ext) {
            ext = JSON.parse(ext);
            this.serverurl = ext['url'] || '';
            this.userid = ext['userid'] || '';
            this.accesstoken = ext['token'] || '';
        }
    };

    this.home = function (filter) {
        var result = { "class": [] };
        var url = this.serverurl + "/Users/" + this.userid + "/Views";
        var views = this.get(url);
        views = views['Items'] || [];
        views.array.forEach(function (element) {
            result["class"].push({ "type_id": element["Id"], "type_name": element["Name"] });
        });
        return JSON.stringify(result);
    };

    this.homeVod = function () {
        var result = {};
        return JSON.stringify(result);
    };

    this.category = function(tid, pg, filter, obj){
        var result = {};
        return JSON.stringify(result);
    };

    this.getXEmbyAuthorization = function () {
        var XEmbyAuthorization = "MediaBrowser Client=\"CatTv\", Device=\"CatTv\", DeviceId=\"TW96aWxsYS81LjAgKFdpbmRvd3MgTlQgNi4xOyBXa\", Version=\"10.8.1\"";
        XEmbyAuthorization += ", Token=\"" + this.accesstoken + "\"";
        return XEmbyAuthorization;
    };

    this.send = function (url, data, method) {
        var httpRequest = new XMLHttpRequest();
        httpRequest.open(method, url, false);
        httpRequest.setRequestHeader("Content-type", "application/json");
        httpRequest.setRequestHeader("X-Emby-Authorization", this.getXEmbyAuthorization())
        httpRequest.send(data);

        var rsp = {};
        if (httpRequest.status === 200) {
            console.log(request.responseText);
            rsp = JSON.parse(request.responseText);
        }

        return rsp;
    };

    this.get = function (url) {
        return this.send(url, null, 'GET');
    };

    this.post = function (url, data) {
        return this.send(url, data, 'POST');
    };
})()
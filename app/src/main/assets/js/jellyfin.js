export default {
    serverurl: '',
    userid: '',
    accesstoken: '',
    self: this,

    init: function (ext) {
        console.log("ext:", ext);
        if (ext) {
            ext = JSON.parse(ext);
            this.serverurl = ext['url'] || '';
            this.userid = ext['userid'] || '';
            this.accesstoken = ext['token'] || '';
        }
    },

    home: function (filter) {
        var result = { "class": [] };
        var url = "/Users/" + this.userid + "/Views";
        var views = this.get(url);
        console.log('jellyfin-js', views);
        views = views['Items'] || [];
        console.log('jellyfin-js', views);
        views.forEach(function (element) {
            result["class"].push({ "type_id": element["Id"], "type_name": element["Name"] });
        });
        result = JSON.stringify(result);
        console.log(result);
        return result
    },

    homeVod: function () {
        var result = {};
        return JSON.stringify(result);
    },

    category: function (tid, pg, filter, obj) {
        var result = {};
        var limit = 100;
        var pageCount = 1;
        var page = pg;
        var totalCount = 0;

        if (page == 0) page = 1;
        var url = "/Users/" + this.userid + "/Items/" + tid;
        var Collection = this.get(url);
        if (!Collection) return "{}"

        var Type = Collection["CollectionType"] || '';
        var ItemsUrl = "/Users/" + this.userid + "/Items?ParentId=" + tid + "&Limit=" + limit;
        ItemsUrl += "&Recursive=true&Fields=PrimaryImageAspectRatio,BasicSyncInfo,Seasons,Episodes&ImageTypeLimit=1";
        ItemsUrl += "&EnableImageTypes=Primary,Backdrop,Banner,Thumb";
        ItemsUrl += "&SortBy=DateCreated,SortName,ProductionYear";

        console.log(Type);
        if (Type === "tvshows") {
            console.log("这是电视");
            ItemsUrl += "&IncludeItemTypes=Series";
        } else if (Type === "movies") {
            console.log("这是电影");
            ItemsUrl += "&IncludeItemTypes=Movie";
        } else {
            console.log("这是电影电视");
            ItemsUrl += "&IncludeItemTypes=Movie,Series";
        }
        var startIndex = page * limit - limit;
        ItemsUrl += "&StartIndex=" + startIndex;

        console.log(ItemsUrl);
        var Items = this.get(ItemsUrl);
        var totalCount = Items["TotalRecordCount"];
        Items = Items['Items'] || [];
        console.log(Items);
        var videos = [];
        var superServerurl = this.serverurl;
        Items.forEach(function (ele) {
            var itemid = ele["Id"];
            var imgurl = ele["ImageTags"]["Primary"];
            imgurl = superServerurl + "/Items/" + itemid + "/Images/Primary?fillHeight=286&fillWidth=200&quality=96&tag=" + imgurl;
            videos.push({
                "vod_id": itemid,
                "vod_name": ele["Name"],
                "vod_pic": imgurl,
                "vod_remarks": ""
            });
        });
        result["page"] = pg;
        result["pagecount"] = Math.ceil(totalCount / limit);
        result["limit"] = limit;
        result["total"] = totalCount;
        result["list"] = videos;
        result = JSON.stringify(result);
        console.log(result);
        return result;
    },

    detail: function (ids) {
        var detailUrl = "/Users/" + this.userid + "/Items/" + ids;

        var detail = this.get(detailUrl);
        if (!detail) return "{}"

        let vodid = detail["Id"];
        let vodname = detail['Name']
        let vod = {
            "vod_id": vodid,
            "vod_name": vodname,
            "vod_pic": this.getPrimaryImgUrl(detail),
            "type_name": detail["Genres"],
            "vod_year": detail["ProductionYear"] || '',
            "vod_area": "",
            "vod_remarks": detail["CommunityRating"],
            "vod_actor": "", //演员
            "vod_director": "",   //导演
            "vod_content": detail["Overview"],
            "vod_play_from": "Jellyfin",
            "vod_play_url": detail["Type"] === "Series" ? getEpisodes(vodid) : (vodname + "$" + vodid),
        }

        let result = {
            'list': [vod]
        }
        result = JSON.stringify(result);
        console.log(result);
        return JSON.stringify(vod);
    },

    getEpisodes: function (SeriesId) {
        let SeasonsUrl = "/Shows/" + SeriesId + "/Seasons?userId=" + this.userid;
        SeasonsUrl += "&Fields=ItemCounts,PrimaryImageAspectRatio,BasicSyncInfo,MediaSourceCount";

        let Seasons = this.get(SeasonsUrl);
        if (!Seasons) return "";

        Seasons = Seasons["Items"] || [];
        var superServerurl = this.serverurl;
        var superUserid = this.userid;
        var self = this;
        let result = [];
        Seasons.forEach((ele) => {
            let seasonName = ele["Name"];
            let seasonId = ele["Id"];
            let EpisodesUrl = "/Shows/" + SeriesId + "/Episodes?seasonId=" + seasonId;
            EpisodesUrl += "&userId=" + superUserid;
            EpisodesUrl += "&Fields=ItemCounts,PrimaryImageAspectRatio,BasicSyncInfo,CanDelete,MediaSourceCount,Overview";
            let Episodes = self.get(EpisodesUrl);
            if (!Episodes) return;
            Episodes = Episodes["Items"];
            for (var j = 0; j < Episodes.length(); j++) {
                Episode = Episodes[j];
                let EpisodeId = Episode["Id"];
                let EpisodeName = Episode["Name"];
                let PlayUrl = EpisodeId;
                result.push(seasonName + "_" + EpisodeName + "$" + PlayUrl);
            }
        });

        result = result.join("#")
        console.log(result);
        return result;
    },

    search: function (key, quick) {
        let searchUrl = JYFUrl + "/Users/" + this.userid + "/Items?searchTerm=" + key;
        searchUrl += "&Limit=24&Fields=PrimaryImageAspectRatio,CanDelete,BasicSyncInfo,MediaSourceCount";
        searchUrl += "&Recursive=true&EnableTotalRecordCount=false&ImageTypeLimit=1&IncludePeople=false";
        searchUrl += "&IncludeMedia=true&IncludeGenres=false&IncludeStudios=false&IncludeArtists=false";
        searchUrl += "&IncludeItemTypes=Movie,Series";

        let searchResult = this.get(this.serverurl);
        let result = { "list": [] }
        if (!searchResult) return JSON.stringify(result);
        try {
            searchResult = searchResult["Items"];
            for (var i = 0; i < searchResult.length(); i++) {
                let v = searchResult(i);
                result["list"].push({
                    "vod_id": v["Id"],
                    "vod_name": v["Name"],
                    "vod_pic": this.getPrimaryImgUrl(v),
                    "vod_remarks": ""
                });
            }
        } catch (e) {
            console.log(e)
        }

        result = JSON.stringify(result);
        return result;
    },

    play: function (flag, id, vipFlags) {
        let playUrl = this.serverurl + "/videos/" + id + "/stream.mp4?static=true&a";
        let result = {
            "parse": 0,
            "playUrl": playUrl,
            "url": id
        }

        result = JSON.stringify(result);
        return result;
    },

    getPrimaryImgUrl: function (item) {
        let PrimaryUrl = item["ImageTags"]["Primary"] || "";
        let url = this.serverurl + "/Items/" + item["Id"] + "/Images/Primary";
        url += "?fillHeight=286&fillWidth=200&quality=96&tag=" + PrimaryUrl;
        return url;
    },

    getXEmbyAuthorization: function () {
        var XEmbyAuthorization = "MediaBrowser Client=\"CatTv\", Device=\"CatTv\", DeviceId=\"TW96aWxsYS81LjAgKFdpbmRvd3MgTlQgNi4xOyBXa\", Version=\"10.8.1\"";
        XEmbyAuthorization += ", Token=\"" + this.accesstoken + "\"";
        return XEmbyAuthorization;
    },

    send: function (url, data, method) {
        url = this.serverurl + url;
        var opt = {
            'method': method,
            'headers': { "X-Emby-Authorization": this.getXEmbyAuthorization() },
            'data': data,
            'redirect': 0
        }

        var rsp = req(url, opt);
        var rspheader = rsp.headers;
        var rspcontent = rsp.content;
        rspcontent = JSON.parse(rspcontent);
        return rspcontent;
    },

    get: function (url) {
        return this.send(url, null, 'get');
    },

    post: function (url, data) {
        return this.send(url, data, 'post');
    }
};
# TestHistorySearch
* 使用自定义FlowLayout和TextView实现搜索历史记录展示；
* 为了限制每次展示搜索历史的个数，并且最近搜索的展示在最前面，使用List<>保存搜索历史，并存储到SharedPreferences中；
* List每次保存搜索历史需要去重，然后再保存；
* SharedPreferences只能保存基本数据类型，如String，int等，因此编写工具类StorageListSPUtils对List集合数据进行存取等操作；
* ![image](https://github.com/henryneu/TestHistorySearch/blob/master/app/src/main/assets/search_history.jpeg)

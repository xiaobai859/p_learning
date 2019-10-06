app.controller("searchController", function ($scope, $location, searchService) {

  // 定义搜索对象的结构
  $scope.searchMap = {
    "keywords": "",
    "category": "",
    "brand": "",
    "spec": {},
    price: "",
    "pageNum": 1,
    "pageSize": 20,
    "sort": "",
    "sortField": ""
  };
  $scope.resultMap = {};
  //搜索
  $scope.search = function () {
    $scope.searchMap.pageNum = parseInt($scope.searchMap.pageNum);
    searchService.search($scope.searchMap).success(
        function (response) {
          $scope.resultMap = response;//搜索返回的结果
          buildPageLabel(); // 创建分页栏 
          console.log($scope.resultMap);
        }
    );
  }

  // 创建分页栏
  buildPageLabel = function () {
    $scope.pageLabel = [];
    var firstPage = 1; // 开始页码
    var lastPage = $scope.resultMap.totalPages; // 最后页码
    $scope.firstDot = true; // 前面没有点
    $scope.lastDot = true; // 后面没有点
    // 设置分页显示的始末页码
    if (lastPage > 5) { // 总页数超过五页进行处理，否则之间显示页数
      if ($scope.searchMap.pageNum <= 3) { // 当前页小于3 ，只显示前五页
        lastPage = 5;
        $scope.firstDot = false;
      } else if ($scope.searchMap.pageNum >= lastPage - 2) { // 当前页为倒数第1 、2 、3页，只显示最后五页
        firstPage = lastPage - 4;
        $scope.lastDot = false;
      } else { // 当前页处在中间，取以当前页为中心的五页显示
        firstPage = $scope.searchMap.pageNum - 2;
        lastPage = $scope.searchMap.pageNum + 2;
      }
    } else {
      $scope.firstDot = false; // 前面没有点
      $scope.lastDot = false; // 后面没有点
    }

    // 根据始末显示页码
    for (var i = firstPage; i <= lastPage; i++) {
      $scope.pageLabel.push(i);
    }

  }

  // 添加搜索项
  $scope.addSearchItem = function (key, value) {
    if (key == "category" || key == "brand" || key == "price") { // 点击分类或品牌
      $scope.searchMap[key] = value;
    } else { // 点击规格
      $scope.searchMap.spec[key] = value;
    }
    $scope.search(); // 查询
  }

  // 撤销搜索项
  $scope.removeSearchItem = function (key, value) {
    if (key == "category" || key == "brand" || key == "price") { // 点击分类或品牌
      $scope.searchMap[key] = "";
    } else { // 点击规格
      delete $scope.searchMap.spec[key];
    }
    $scope.search(); // 查询
  }

  // 分页查询
  $scope.queryByPage = function (pageNum) {
    if (pageNum < 1 || pageNum > $scope.resultMap.totalPages) { // 点击第一页或最后一页时设置页面保持不变
      return;
    }
    $scope.searchMap.pageNum = pageNum;
    $scope.search(); // 查询
  }

  // 首页不可用样式
  $scope.isTopPage = function () {
    if ($scope.searchMap.pageNum == 1) {
      return true;
    } else {
      return false;
    }
  }

  // 尾页不可用样式
  $scope.isEndPage = function () {
    if ($scope.searchMap.pageNum == $scope.resultMap.totalPages) {
      return true;
    } else {
      return false;
    }
  }

  // 排序查询
  $scope.querySearch = function (sort, sortField) {
    $scope.searchMap.sort = sort;
    $scope.searchMap.sortField = sortField;

    $scope.search(); // 查询
  }

  // 判断关键字是否是品牌
  $scope.keywordsIsBrand = function () {
    for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
      if ($scope.searchMap.keywords.search($scope.resultMap.brandList[i].text)
          == 0) { // 
        return true;
      }
    }
    return false;
  }

  // 视频提供的方法
  /*$scope.keywordsIsBrand=function(){
    for(var i=0;i<$scope.resultMap.brandList.length;i++){
      if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) != -1){ js中字符串indexOf()方法查找，当前字符串是否包含其他字符串，包含返回首次出现的索引，不包含则返回-1
        return true;
      }			
    }		
    return false;
  }*/

  // 首页传递参数搜索
  $scope.loadKeywords = function () {
    $scope.searchMap.keywords = $location.search()['keywords'];
    $scope.search(); // 查询
  }

});





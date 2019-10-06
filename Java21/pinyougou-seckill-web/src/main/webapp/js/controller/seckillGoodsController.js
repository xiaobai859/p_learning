app.controller("seckillGoodsController",
    function ($scope, $location, $interval, seckillGoodsService) {

      // 查询秒杀商品列表
      $scope.findList = function () {
        seckillGoodsService.findList().success(
            function (response) {
              console.log(response);
              $scope.list = response;
            }
        );
      }

      // 查询秒杀商品信息
      $scope.findOne = function () {
        id = $location.search()["id"];
        seckillGoodsService.findOneFromRedis(id).success(
            function (response) {
              $scope.entity = response;
              console.log($scope.entity);
              console.log(new Date($scope.entity.endTime).getTime());
              allSecond = Math.floor((new Date($scope.entity.endTime).getTime()
                  - new Date().getTime()) / 1000); // 剩余秒数

              time = $interval(function () {
                allSecond = allSecond - 1;
                $scope.timeString = converTimeString(allSecond);
                if ($scope.second <= 0) {
                  $interval.cancel(time);
                }
              }, 1000);
            }
        );
      }

      // 秒数转换为 天 时：分：秒字符串方法
      converTimeString = function (allSecond) {
        var days = Math.floor(allSecond / (60 * 60 * 24)); // 天数

        var hours = Math.floor(allSecond % (60 * 60 * 24) / (60 * 60)); // 小时数
        var minutes = Math.floor(allSecond % (60 * 60 * 24) % (60 * 60) / 60); // 分钟
        var seconds = allSecond % (60 * 60 * 24) % (60 * 60) % 60; // 秒数
        var timeString = "";
        if (days > 0) {
          timeString = days + "天";
        }

        hours = hours < 10 ? "0" + hours : hours;
        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        return timeString += hours + ":" + minutes + ":" + seconds + "秒";
      }

      $scope.submitOrder = function () {
        seckillGoodsService.submitOrder($scope.entity.id).success(
            function (response) {
              if (response.success) { // 下单成功
                alert("请在5分钟之内完成支付");
                location.href = "pay.html"; // 跳转到支付页面
              } else {
                alert(response.message);
              }
            }
        );
      }

    });

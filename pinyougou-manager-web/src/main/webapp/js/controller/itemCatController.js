 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;
				$scope.entity.typeIds = JSON.parse($scope.entity.typeIds) // 转换类型列表
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			$scope.entity.parentId = $scope.parentId; // 保存是设置上级ID
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
					$scope.findByParentId($scope.parentId)// 保存成功后根据上级ID重新进行查询
		        //	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	$scope.parentId = 0; // 用于记录上级ID 默认值为0 	
	// 根据上级id返回列表
	$scope.findByParentId=function(parentId) {
		$scope.parentId = parentId; // 记录上级ID	
		itemCatService.findByParentId(parentId).success(
			function(response) {
				$scope.list = response;
			}	
		);
	}
	
	// 设置面包屑
	$scope.grade = 1; // 默认级别为1

	
	// 点击后设置级别
	$scope.setGrade=function(value) {
		$scope.grade = value;
	}
	
	$scope.selectList=function(click_entity) {

		if($scope.grade == 1) { // 一级目录
			$scope.entity_1 = null;
			$scope.entity_2 = null;
		}
		if($scope.grade == 2) { // 二级目录
			$scope.entity_1 = click_entity;
			$scope.entity_2 = null;
		}
		if($scope.grade == 3) { // 三级目录
			$scope.entity_2 = click_entity;
		}
		
		$scope.findByParentId(click_entity.id);
	}
	
	$scope.typeTemplateList={data:[]};
	// 读取类型列表
	$scope.selectOptionList=function() {
		typeTemplateService.selectOptionList.success(
			function(response){
				$scope.typeTemplateList={data:response};
			}
				
		);
	}
    
});	

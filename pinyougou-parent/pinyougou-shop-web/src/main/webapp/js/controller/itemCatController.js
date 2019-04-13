 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService,brandService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承




    /**
	 * 查找目录
     * @param parentId
     */
    $scope.parentId=0;

    $scope.findByParentId=function (parentId) {
        itemCatService.findByParentId(parentId).success(
            function (response) {
            	$scope.parentId=parentId;
                $scope.list=response;
            }
        )
    }

    /**
	 * 点击下级变量记数
     * @type {number}
     */
    //面包屑当前级别
    $scope.grade=1;
	//修改当前级别
    $scope.setGrade=function (value) {
        $scope.grade=value;
    }
	// 面包屑

	$scope.selectList=function(p_entity){
    	if ($scope.grade==1){
    		$scope.entity_1=null;
    		$scope.entity_2=null;
		} else if ($scope.grade==2){
    		$scope.entity_1=p_entity;
    		$scope.entity_2=null;
		} else if ($scope.grade==3){
    		$scope.entity_2=p_entity;
		}
		this.findByParentId(p_entity.id);
	}


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
			}
		);				
	}


	//保存

	$scope.save=function(){

		var serviceObject;//服务层对象
		var parentId;
		if ($scope.entity_2!=null){
            parentId=$scope.entity_2.id;
		}else if ($scope.entity_1!=null){
            parentId=$scope.entity_1.id;
		}else {
            parentId=0;
		}
		$scope.entity.parentId=parentId;
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity);//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findByParentId($scope.parentId)//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框
       // alert(JSON.stringify($scope.selectIds))
        itemCatService.dele( $scope.selectIds ).success(

			function(response){
				if(response.success){
                    $scope.findByParentId($scope.parentId)//重新加载
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

    //列表
    $scope.typeTemplateList={data:[]};
    $scope.findTypeTemplateList=function(){
        typeTemplateService.findAll().success(function (response) {
            for (var k = 0; k < response.length; k++) {
                delete response[k]["specIds"];
                delete response[k]["brandIds"];
                delete response[k]["customAttributeItems"];
                delete response[k]["name"];
            }
            $scope.typeTemplateList={data:response};


        })
    }
});	

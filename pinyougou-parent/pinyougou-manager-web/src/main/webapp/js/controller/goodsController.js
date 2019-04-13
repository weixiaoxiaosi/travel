 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService2,itemCatService2,typeTemplateService){

	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService2.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService2.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};//定义页面实体结构

	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService2.update( $scope.entity ); //修改
		}else{
			serviceObject=goodsService2.add( $scope.entity  );//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService2.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
                    $scope.selectIds=[];//清空ID集合
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 


    //查询实体
    $scope.findOne=function(){
        //获取前端参数
        var id = $location.search()['id'];
        if (id!= undefined){
            if (id !=null){
                goodsService2.findOne(id).success(
                    function(response){
                        $scope.entity= response;
                        //显示富文本编辑里面的描述
                        editor.html($scope.entity.goodsDesc.introduction);

                        //显示图片信息
                        $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);

                        //显示扩展信息
                        $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);

                        //显示规格信息
                        $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);

                        //显示SKU规格列表
                        //SKU列表规格列转换
                        for (var i= 0 ; i <$scope.entity.itemList.length;i++){
                            $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec)
                        }
                    }

                );
            }
        }
        return ;
    }
	//搜索
	$scope.search=function(page,rows){			
		goodsService2.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    $scope.status=['未审核','已审核','审核未通过','关闭'];
    //前端代码用ID去查询后端，异步返回商品分类名称。
    $scope.itemCatList=[];
    $scope.findItemCatList=function () {
        itemCatService2.findAll().success(function (response) {
            for (var i = 0 ; i <response.length; i++){
                $scope.itemCatList[response[i].id]=response[i].name;
            }

        })
    }


    $scope.selectItemCat1List=function(){
        itemCatService2.findByParentId(0).success(function (response) {
            $scope.itemCat1List=response;
        })
    }

    $scope.$watch("entity.goods.category1Id",function(newValue,oldValue) {

        if(newValue!=undefined){
            itemCatService2.findByParentId(newValue).success(function (response) {
                $scope.itemCat2List = response;
            })

        }

    })

    $scope.$watch("entity.goods.category2Id",function(newValue,oldValue) {

        if(newValue!=undefined) {
            itemCatService2.findByParentId(newValue).success(function (response) {
                $scope.itemCat3List = response;
            })
        }
    })

    $scope.$watch("entity.goods.category3Id",function(newValue,oldValue) {
        if (newValue!=undefined){
            itemCatService2.findOne(newValue).success(function (response) {
                $scope.entity.goods.typeTemplateId= response.typeId;
            })
        }

    })
    $scope.$watch("entity.goods.typeTemplateId",function(newValue,oldValue) {
        if (newValue!=undefined){
            typeTemplateService.findOne(newValue).success(function (response) {
                $scope.typeTemplate=response;
                $scope.typeTemplate.brandIds=JSON.parse(response.brandIds)
                if ($location.search()['id']==null){
                    $scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.customAttributeItems)
                }
                typeTemplateService.findSpecList(newValue).success(function (response) {
                    $scope.specList=response;
                })
            })
        }

    })
    /*
     * 勾选页面上的规格时调用此函数
     * @param $event 当前点击的checkbox
     * @param specName 规格的名称
     * @param optionName 规格选项的值
        */
    $scope.updateSpecAttribute=function($event,specName,optionName){
        //查找规格有没有保存过
        var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',specName);
        //找到相关记录
        if (obj==null){
            $scope.entity.goodsDesc.specificationItems.push(
                {'attributeName':specName,'attributeValue':[optionName]});
        }else {
            //如果已选中
            if ($event.target.checked){
                obj.attributeValue.push(optionName)
            }else {
                //取消勾选
                //查找当前value的下标
                var index = obj.attributeValue.indexOf(optionName);
                obj.attributeValue.splice(index,1)
                //删除数据
                //取消勾选后，如果当前列表里没有记录时，删除当前整个规格
                if (obj.attributeValue.length < 1 ){
                    var indexObj = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
                    $scope.entity.goodsDesc.specificationItems.splice(indexObj,1)
                }
            }
        }
        //刷新sku列表

    }

    // 1. 创建$scope.createItemList方法，同时创建一条有基本数据，不带规格的初始数据
    $scope.createItemList=function(){
        // 参考: $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' }]
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' }]
        // 2. 查找遍历所有已选择的规格列表，后续会重复使用它，所以我们可以抽取出个变量items
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0 ;i<items.length;i++){
            // 9. 回到createItemList方法中，在循环中调用addColumn方法，并让itemList重新指向返回结果;
            $scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue)
        }
    }

    // 3. 抽取addColumn(当前的表格，列名称，列的值列表)方法，用于每次循环时追加列
    addColumn=function(list,specName,optionName){
        // 4. 编写addColumn逻辑，当前方法要返回添加所有列后的表格，定义新表格变量newList
        var newlist = [];
        // 5. 在addColumn添加两重嵌套循环，一重遍历之前表格的列表，二重遍历新列值列表

        for(var i =0 ; i<list.length;i++){
            for (var j = 0 ;j<optionName.length; j++){
                // 6. 在第二重循环中，使用深克隆技巧，把之前表格的一行记录copy所有属性，
                // 用到var newRow = JSON.parse(JSON.stringify(之前表格的一行记录));
                var newRow = JSON.parse(JSON.stringify(list[i]));
                // 7. 接着第6步，向newRow里追加一列
                newRow.spec[specName] = optionName[j];
                // 8. 把新生成的行记录，push到newList中
                newlist.push(newRow)
            }
        }
        return newlist;
    }

    /**
     * 验证规格选项是否要勾选
     * @param specName 规格名称
     * @param optionName 规格项名称
     * @returns {boolean}
     */

    $scope.checkAttributeValue=function (specName,optionName) {
        //用户已经勾选的规格信息
        var items = $scope.entity.goodsDesc.specificationItems;
        var obj = $scope.searchObjectByKey(items,'attributeName',specName)
        if (obj!=null){
            //检查选项是否存在
            if (obj.attributeValue.indexOf(optionName) > -1){
                return true;
            }
        }
        return false;
    }

    //通过id修改goods里面的审核状态
    $scope.updateStatus=function (status) {
        goodsService2.updateStatus($scope.selectIds,status).success(function (response) {
            if (response.success){
                $scope.reloadList();//刷新列表
                $scope.selectIds=[];//清空ID集合
            }else {
                alert(response.message);
            }
        })
    }


});

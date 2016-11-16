var app = angular.module("Outlook4All", ['ui.bootstrap','angular-loading-bar','ngStorage'],function($locationProvider)
{
      $locationProvider.html5Mode({
      enabled: true,
      requireBase: false
      });
    });

app.config(['$compileProvider' , function ($compileProvider)
    {
          $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|http):.*/);
    }]);

app.run(function($http,$localStorage,$rootScope) {
    if ($localStorage.types) {
        $rootScope.dataTypes = $localStorage.types;
    } else {
   $http.get('/types').success(function(data){
        $rootScope.data = data;
        $localStorage.types = data;
   });
   }
    $rootScope.updateTypes=function(typeToUpdate) {
        for (dtype in $localStorage.types) {
            if ($localStorage.types[dtype].type == typeToUpdate)
                $localStorage.types[dtype].enabled = !$localStorage.types[dtype].enabled;
        }
    }

    $rootScope.getTypesString=function() {
        ret = "";
        for (dtype in $localStorage.types) {
            if ($localStorage.types[dtype].enabled)
                ret += ':'+$localStorage.types[dtype].type;
        }
        return ret;
    }

});

app.controller("LandingPageController", function($scope, $window,$http, $location, $localStorage) {
$scope.types=$localStorage.types;

$scope.clickType = function(typeClicked) {
    $scope.updateTypes(typeClicked);
    $scope.getStats();
   }

$scope.init = function () {
    // check if there is query in url
    // and fire search in case its value is not empty
};

$scope.getStats = function() {
    typesParam = $scope.getTypesString();
    $http.get('/stats/'+typesParam).
    success(function(data, status, headers, config) {
      $scope.topDiscussedEver = data["topDiscussedEver"];
      $scope.topPostersEver = data["topPostersEver"];
      $scope.recent = data["recent"];
      $scope.emailsCount = data["emailsCount"];
      $scope.emailsSize = data["emailsSize"];
      $scope.attachmentsCount = data["attachmentsCount"];
      $scope.attachmentsSize = data["attachmentsSize"];
      $scope.firstPost = data["firstPost"];
      $scope.lastPost = data["lastPost"];
    }).
    error(function(data, status, headers, config) {
      // log error
    });
}

$scope.search = function (query) {
    console.log(this.query);
    var q = encodeURIComponent(this.query);
    $window.location.href='/search.html?q='+q;
};

$scope.getStats();
});

app.controller("ConversationController", function($scope, $window,$http, $location, $localStorage) {
$scope.types=$localStorage.types;

$scope.clickType = function(typeClicked) {
    $scope.updateTypes(typeClicked);
    $scope.getConv(typeClicked);
   }


$scope.getConv = function () {
    typesParam = $scope.getTypesString();
    $http.get('/getconv/'+escape($location.search().q)+'/'+typesParam).
        success(function(data, status, headers, config) {
        $scope.eventDetailsCollapseMap=[];
        $scope.conversations = data;
    }).
    error(function(data, status, headers, config) {
      // log error
    });
}
$scope.search = function (query) {
    console.log(this.query);
    var q = encodeURIComponent(this.query);

    $window.location.href='/search.html?q='+q;
};

$scope.getConv();

// clicking on single row - show email
  $scope.selectTableRow = function (index) {
        if ($scope.eventDetailsCollapseMap[index]  == undefined)
        {
            $scope.eventDetailsCollapseMap[index]=true;

        } else {
            $scope.eventDetailsCollapseMap[index]=!$scope.eventDetailsCollapseMap[index];
        }


     };

});

app.controller("SearchController", function($scope, $window,$http, $location, $localStorage) {

$scope.init = function () {
    // check if there is query in url
    // and fire search in case its value is not empty

    // $scope.query=$location.search().q;
     $scope.types=$localStorage.types;
     if ($location.search().q != undefined )
             $scope.search($location.search().q);

};

$scope.clickType = function(typeClicked) {
    $scope.updateTypes(typeClicked);
    $scope.search($scope.query);
   }

$scope.search = function (query) {
    $scope.currentPage=1;
    $scope.pageSearch(query);

};

$scope.pageSearch = function (query) {

    if (query != undefined)
        $scope.query=query;

    if ((this.currentPage == undefined) | (this.currentPage==1)) {
        this.fromPage=0;
        }
        else {
        this.fromPage = this.currentPage-1;
        }


    typesParam = $scope.getTypesString();

    $http.get('/search/'+$scope.query+'/'+this.fromPage*10+'/'+typesParam).
     success(function(data, status, headers, config) {

          $scope.took = data["took"];
          $scope.hits = data["totalHits"];
          $scope.searchRes = data["searchResults"];
          $window.scrollTo(0,0);


        }).
        error(function(data, status, headers, config) {
          // log error
        });

};

});

app.directive('autoFocus', function($timeout) {
    return {
        restrict: 'AC',
        link: function(_scope, _element) {
            $timeout(function(){
                _element[0].focus();
            }, 0);
        }
    };
});

app.filter("sanitize", ['$sce', function($sce) {
  return function(htmlCode){
    return $sce.trustAsHtml(htmlCode);
  }
}]);


app.filter('escape', function($window) {
  return $window.encodeURIComponent;
});
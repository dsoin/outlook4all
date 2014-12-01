var app = angular.module("Outlook4All", ['ui.bootstrap'],function($locationProvider)
{
      $locationProvider.html5Mode({
      enabled: true,
      requireBase: false
      });
    });


app.controller("LandingPageController", function($scope, $window,$http, $location) {

$scope.init = function () {
    // check if there is query in url
    // and fire search in case its value is not empty
};

$http.get('/stats').
 success(function(data, status, headers, config) {



      $scope.topDiscussedEver = data["topDiscussedEver"];
      $scope.topPostersEver = data["topPostersEver"];
      $scope.recent = data["recent"];



    }).
    error(function(data, status, headers, config) {
      // log error
    });
$scope.search = function (query) {
    console.log(this.query);
    var q = encodeURIComponent(this.query);


    $window.location.href='/search.html?q='+q;
};

});

app.controller("ConversationController", function($scope, $window,$http, $location) {

$scope.init = function () {
    // check if there is query in url
    // and fire search in case its value is not empty
};

$http.get('/getconv/'+$location.search().q).
 success(function(data, status, headers, config) {

    $scope.eventDetailsCollapseMap=[];
      $scope.conversations = data;



    }).
    error(function(data, status, headers, config) {
      // log error
    });
$scope.search = function (query) {
    console.log(this.query);
    var q = encodeURIComponent(this.query);


    $window.location.href='/search.html?q='+q;
};

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

app.controller("SearchController", function($scope, $window,$http, $location) {

$scope.init = function () {
    // check if there is query in url
    // and fire search in case its value is not empty

    // $scope.query=$location.search().q;

      if ($location.search().q != undefined )
             $scope.search($location.search().q);

};

$scope.search = function (query) {

    if (query != undefined)
        $scope.query=query;

    if ((this.currentPage == undefined) | (this.currentPage==1)) {
        this.fromPage=0;
        }
        else {
        this.fromPage = this.currentPage-1;
        }

    $http.get('/search/'+$scope.query+'/'+this.fromPage*10).
     success(function(data, status, headers, config) {

          $scope.took = data["com.fortify.techsupport.o4a.beans.SearchResultsBean"]["took"];
          $scope.hits = data["com.fortify.techsupport.o4a.beans.SearchResultsBean"]["totalHits"];
          $scope.searchRes = data["com.fortify.techsupport.o4a.beans.SearchResultsBean"]["searchResults"][0]["com.fortify.techsupport.o4a.beans.SearchBean"];
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
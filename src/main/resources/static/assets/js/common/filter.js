define([
  'model',
  'data',
  'tool',
  'public',
  'viewTpl/filter/index',
  'viewTpl/filter/condition',
  'viewTpl/filter/listTpl'
], function (model, dataApp, tool, publicApp, filterTpl, conditionTpl, listTpl) {
  var filterApp = {
    module: "",
    selectedData: {},
    init: function (callback, moduleName) {
      this.events();
      this.getFilterModal(callback, moduleName);
    },

    initSelect: function (module) {
      var str = module === 'statistics' ? '统计日期' : '日志日期';
      $(".pjNameSelect").dropdownSelect({isMiss: true, placeholder: "项目名称"});
      $(".pjLocationSelect").dropdownSelect({isMiss: true, placeholder: "安装地区"});
      $(".errorLevelSelect").dropdownSelect({isMiss: true, placeholder: "错误级别"});
      $(".tagSelect").dropdownSelect({isMiss: true, placeholder: "语句属性"});
      $(".durationSelect").dropdownSelect({isMiss: true, placeholder: "查询耗时"});
      $(".errorSqlTypeSelect").dropdownSelect({isMiss: true, placeholder: "不合格sql分类"});
      $(".timeTagSelect").dropdownSelect({isMiss: true, placeholder: str});
      $(".environmentSelect").dropdownSelect({isMiss: true, placeholder: "环境信息"});
      $(".feedbackSelect").dropdownSelect({isMiss: true, placeholder: "授权反馈"});
      $(".roleSelect").dropdownSelect({isMiss: true, placeholder: "角色"});
      $(".stateSelect").dropdownSelect({isMiss: true, placeholder: "状态"});
    },

    /** 初始化新建和编辑的日期时间范围 **/
    initCustomTimeTag: function (options) {
      var $this = options.$this;
      var $closest = $this.closest(".form-cro-expand");
      var id = $closest.find(".form-validate").attr("id");

      if (options.value === "custom") {
        $closest.find(".custom-log-date").addClass("active");
        var opt = {
          elem: '#' + id + ' .custom-log-date .form-control',
          isInitDateTime: true,
          range: "~",
          done: function (value, date, endDate) {

            dataApp.filterObj[id].startTime = tool.getAbsoluteSecond(date);
            dataApp.filterObj[id].endTime = tool.getAbsoluteSecond(endDate);
          }
        };

        publicApp.initDateTime(opt);
      } else {
        $closest.find(".custom-log-date").removeClass("active");
      }
    },

    getFilterModal: function (callback, moduleName) {
      var self = this;
      if (!dataApp.filterGroups.pjName) {
        if (moduleName === "authorization") {
          model.getPjNameAndCitiesByAuthorization().then(function (res) {
            if (tool.checkStatusCode(res.code)) {
              dataApp.allFilterData = res.data;
              self.setFilterData(res.data, callback);
            } else {
              publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
            }
          });
        } else {
          model.getAllFilter().then(function (res) {
            if (tool.checkStatusCode(res.code)) {
              dataApp.allFilterData = res.data;
              self.setFilterData(res.data, callback);
            } else {
              publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
            }
          });
        }
      } else {
        callback && callback();
      }
    },

    setFilterData: function (data, callback) {
      var pjName = [], pjLocation = [];
      data.forEach(function (o) {
        pjName.push(o.pjName);
        var list = o.pjLocationList === null || o.pjLocationList === undefined ? [] : o.pjLocationList;
        list.forEach(function (location) {
          if (pjLocation.indexOf(location) === -1) {
            pjLocation.push(location);
          }
        });
      });

      dataApp.filterGroups.pjNames = pjName;
      dataApp.filterGroups.pjLocation = pjLocation;
      callback && callback();
    },

    getFilterDom: function (opt) {
      this.module = opt;
      var data = $.extend(true, {}, dataApp.filterGroups, opt);
      return filterTpl(data);
    },

    setFilterDom: function ($id, opt) {
      $id.html(this.getFilterDom(opt));
      this.initSelect(opt.module);
    },

    setFilterObj: function (opt) {
      var $this = opt.$this;
      var $closest = $this.closest(".form-cro-expand");
      this.changeSelectState(opt);
      this.setFilterCondition($closest);
    },

    changeSelectState: function (opt) {
      var $this = opt.$this;
      var $parent = $this.closest(".dropdownSelect-menu");
      var $closest = $this.closest(".dropdownSelect");
      if (opt.multiple) {
        $closest.toggleClass("open");
        $this.toggleClass('selected');
        if (opt.value !== "all") {
          if ($parent.find('.item:not(".all")').length === $parent.find('.item.selected:not(".all")').length) {
            $parent.find('.item.all').addClass('selected');
          } else {
            $parent.find('.item.all').removeClass('selected');
          }
        } else {
          if ($this.hasClass("selected")) {
            $parent.find('.item').addClass('selected');
          } else {
            $parent.find('.item').removeClass('selected');
          }
        }
      } else {
        $closest.find('[data-target="#"]').dropdownSelect("setChecked", {
          value: $this.attr('data-value'),
          text: $this.find('>a').text()
        });
        $parent.find('.item').removeClass('selected');
        $this.addClass('selected');

        if (opt.type === "searchTime") {
          this.initCustomTimeTag(opt);
        }
      }
      if (opt.type === "pjName") {
        this.getPjLocationList($this);
      }
      if (opt.type === "pjLocation") {
        this.getPjNameList($this);
      }
    },
//选择项目名称后，获取当前选择的项目对应地区
    getPjLocationList: function ($this, projectName) {
      var self = this;
      var listItem = [];
      var condition = {};
      var data = {};
      if (projectName !== "" && projectName !== undefined) {
        var obj = {};
        var arr = [];
        obj.value = projectName;
        obj.text = projectName;
        arr.push(obj);
        data.pjName = arr;
      } else {
        data = $this !== undefined && $this !== "" ? self.getSelectFilterData($this.closest(".form-cro-expand")) : data;
      }

      var currentPjName = data.pjName !== undefined ? data.pjName : [];
      if (currentPjName.length > 0) {
        currentPjName.forEach(function (name) {
          for (var i = 0; i < dataApp.allFilterData.length; i++) {
            if (name.value === dataApp.allFilterData[i].pjName) {
              var currentPjLocation = dataApp.allFilterData[i].pjLocationList === undefined || dataApp.allFilterData[i].pjLocationList === null ? [] : dataApp.allFilterData[i].pjLocationList;
              currentPjLocation.forEach(function (location) {
                if (listItem.indexOf(location) === -1) {
                  listItem.push(location);
                }
              });
              break;
            }
          }
        });
      } else {
        dataApp.allFilterData.forEach(function (o) {
          var currentPjLocation = o.pjLocationList === undefined || o.pjLocationList === null ? [] : o.pjLocationList;
          currentPjLocation.forEach(function (location) {
            if (listItem.indexOf(location) === -1) {
              listItem.push(location);
            }
          });
        });
      }
      condition.list = listItem;
      $(".pjLocationList").html(listTpl(condition));

      if (self.selectedData.pjLocation !== undefined) {
        var $pjLocationLi = $(".pjLocationList li");
        var $pjLocationList = $(".pjLocationList");
        self.selectedData.pjLocation.forEach(function (location) {
          for (var i = 0; i < $pjLocationLi.length; i++) {
            var $li = $pjLocationLi.eq(i);
            if ($li.attr("data-value") === location.value) {
              $pjLocationLi.eq(i).addClass("selected");
            }
          }
          if ($pjLocationList.find('.item:not(".all")').length === $pjLocationList.find('.item.selected:not(".all")').length) {
            $pjLocationList.find('.item.all').addClass('selected');
          } else {
            $pjLocationList.find('.item.all').removeClass('selected');
          }
        });
      }
    },
    //选择安装地区后，获取的当前选择地区的项目名称
    getPjNameList: function ($this) {
      var self = this;
      var listItem = [];
      var condition = {};
      var data = self.getSelectFilterData($this.closest(".form-cro-expand"));
      var currentPjLocation = data.pjLocation !== undefined ? data.pjLocation : [];
      if (currentPjLocation.length > 0) {
        currentPjLocation.forEach(function (location) {
          for (var i = 0; i < dataApp.allFilterData.length; i++) {
            var locationList = dataApp.allFilterData[i].pjLocationList === null || dataApp.allFilterData[i].pjLocationList === undefined ? [] : dataApp.allFilterData[i].pjLocationList;
            if (locationList.indexOf(location.value) !== -1 && listItem.indexOf(dataApp.allFilterData[i].pjName) === -1) {
              listItem.push(dataApp.allFilterData[i].pjName);
            }
          }
        });
      } else {
        dataApp.allFilterData.forEach(function (o) {
          if (listItem.indexOf(o.pjName) === -1) {
            listItem.push(o.pjName);
          }
        });
      }
      condition.list = listItem;
      $(".pjNameList").html(listTpl(condition));

      if (self.selectedData.pjName !== undefined) {
        var $pjNameLi = $(".pjNameList li");
        var $pjNameList = $(".pjNameList");
        self.selectedData.pjName.forEach(function (name) {
          for (var i = 0; i < $pjNameLi.length; i++) {
            var $li = $pjNameLi.eq(i);
            if ($li.attr("data-value") === name.value) {
              $pjNameLi.eq(i).addClass("selected");
            }
          }
          if ($pjNameList.find('.item:not(".all")').length === $pjNameList.find('.item.selected:not(".all")').length) {
            $pjNameList.find('.item.all').addClass('selected');
          } else {
            $pjNameList.find('.item.all').removeClass('selected');
          }
        });
      }
    },
    setFilterCondition: function ($closest) {
      var id = $closest.find(".form-validate").attr("id");
      dataApp.filterObj[id] = this.getSelectFilterData($closest);
        // var selectFilterData = this.getSelectFilterData($closest);
      // dataApp.filterObj[id] = $.extend(true, dataApp.filterObj[id], selectFilterData);
      // var filterData = dataApp.filterObj[id];
      // for(var i in selectFilterData ) {
      //   for(var j in filterData) {
      //     if(i === j) {
      //       filterData[j] = selectFilterData[i];
      //     }
      //
      //   }
      // }
      // dataApp.filterObj[id] = filterData;
      // $closest.find('.condition-list').html(conditionTpl(dataApp.filterObj[id]));
      $("#" + id + "Filter").find('.condition-list').html(conditionTpl(dataApp.filterObj[id]));
    },

    getSelectFilterData: function ($closest) {
      var data = {};
      var $filterGroup = $closest.find(".dropdownSelect-menu");

      if ($filterGroup.find(".item.selected").length === 0) {
        data["filterLength"] = 0;
        this.selectedData = data;
        return data;
      }
      $filterGroup.each(function () {
        var type = $(this).attr("data-type");
        data[type] = [];
        $(this).find('.item.selected').each(function () {
          var obj = {};
          obj.value = $(this).attr("data-value");
          obj.text = $(this).find('a').text();
          data[type].push(obj);
        });
      });
      this.selectedData = data;
      return data;
    },

    dropdownSelectSearch: function ($this) {
      var $parent = $this.closest(".dropdownSelect-menu");
      var value = $this.val().toLowerCase();

      if (value === "") {
        $parent.find(".item").show();
        $parent.find(".dropdownSelect-no-data").hide();
      } else {
        $parent.find(".item").hide();
        $parent.find(".dropdownSelect-no-data").show();
        $parent.find('.item:not(".all")').each(function () {
          var v = $(this).text().toLowerCase();
          if (v.indexOf(value) !== -1) {
            $(this).show();
            $parent.find(".dropdownSelect-no-data").hide();
          }
        });
      }
    },

    selectFilter: function ($this) {
      var self = this;
      var $closest = $this.closest(".dropdownSelect");
      var index = $this.index();
      var module = $this.closest(".form-cro-expand").find(".form-validate").attr("id");
      var $parents = $this.closest(".analysis-tab-content").find('[data-type="pjLocation"].dropdownSelect-menu');

      var $parent = $this.closest(".dropdownSelect-menu");

      if (module === "statistics" || module === "tableUseDetail" || module === "allSql" || module === "errorSql" || module === "errorDetail") {
        var type = $parent.attr("data-type");

        if (type === "pjLocation") {
          $parents.each(function () {
            self.setFilterObj({
              $this: $(this).find('.dropdownSelect-menu-list li').eq(index),
              type: $(this).attr("data-type"),
              multiple: !!$(this).attr("data-multiple"),
              value: $(this).find('.dropdownSelect-menu-list li').eq(index).attr("data-value")
            });
          });
        } else {
          self.setFilterObj({
            $this: $this,
            type: $parent.attr("data-type"),
            multiple: !!$parent.attr("data-multiple"),
            value: $this.attr("data-value")
          });
        }
      } else {
        self.setFilterObj({
          $this: $this,
          type: $parent.attr("data-type"),
          multiple: !!$parent.attr("data-multiple"),
          value: $this.attr("data-value")
        });
      }

      $closest.toggleClass("open");

    },

    closeFilterItem: function ($this) {
      var self = this;
      var $parent = $this.closest(".label");
      var $closest = $this.closest(".form-cro-expand");
      var module = $this.closest(".form-cro-expand").find(".form-validate").attr("id");
      var type = $parent.attr('data-type');
      var value = $parent.attr('data-value') || "";

      if (!value) {
        $closest.find('[data-type="' + type + '"].dropdownSelect-menu').find('.item').removeClass('selected');
      } else {
        if ((module === "statistics" || module === "tableUseDetail" || module === "allSql" || module === "errorSql" || module === "errorDetail") && type === "pjLocation") {
          var $tab = $this.closest(".analysis-tab-content").find(".filter-warp");
          var $parents = $tab.find('[data-type="pjLocation"].dropdownSelect-menu');
          $parents.each(function (i) {
            var id = $tab.find(".form-validate").eq(i).attr("id");
            $("#" + id + "Filter .pjLocationList [data-value='" + value + "']").click();
          });
        } else {
          $closest.find('[data-type="' + type + '"].dropdownSelect-menu').find('.item.all').removeClass('selected');
          $closest.find('[data-type="' + type + '"].dropdownSelect-menu').find('[data-value="' + value + '"]').removeClass('selected');
        }
      }

      $closest.find('[data-type="' + type + '"].dropdownSelect-menu').siblings('[data-target="#"]').dropdownSelect("clear");
      if (type === "searchTime") {
        $closest.find(".custom-log-date").removeClass("active");
      }

      self.setFilterCondition($closest);
      if (type === "pjName") {
        self.getPjLocationList($closest);
      }
      if (type === "pjLocation") {
        self.getPjNameList($closest);
      }
    },

    resetFilter: function ($this) {
      var $closest = $this.closest(".form-cro-expand");
      var id = $closest.find(".form-validate").attr("id");

      $closest.find('input').val("");
      $closest.find('.checkbox-beauty input[type=checkbox]').removeAttr("checked");
      $closest.find('li').removeClass("selected");
      dataApp.filterObj[id] = "";
      $closest.find(".custom-log-date").removeClass("active");
      $closest.find('.dropdownSelect-menu').siblings('[data-target="#"]').dropdownSelect("clear");
      $closest.find('.condition-list').html(conditionTpl(dataApp.filterObj[id]));
      this.getPjLocationList($closest);
      this.getPjNameList($this);
    },

    mergeFilter: function (o) {
      var arr = [];
      for (var i = 0, len = o.length; i < len; i++) {
        arr.push(o[i].value);
      }
      return arr.join(',');
    },


    getFilter: function (filterObj) {
      var obj = {}, self = this;
      for (var item in filterObj) {
        obj[item] = tool.isArray(filterObj[item]) ? self.mergeFilter(filterObj[item]) : filterObj[item];
      }
      return obj;
    },

    events: function () {
      var self = this;
      var selectFilter = '[data-action="selectFilter"]';
      $(document).off('click.selectFilter.FPointer').on('click.selectFilter.FPointer', selectFilter, function () {
        self.selectFilter($(this));
      });

      var closeFilterItem = '[data-action="closeFilterItem"]';
      $(document).off('click.closeFilterItem.FPointer').on('click.closeFilterItem.FPointer', closeFilterItem, function () {
        self.closeFilterItem($(this));
      });

      var dropdownSelectSearch = '[data-action="dropdownSelectSearch"]';
      $(document).off('input.dropdownSelectSearch.FPointer ').on('input.dropdownSelectSearch.FPointer', dropdownSelectSearch, function () {
        self.dropdownSelectSearch($(this));
      });

      var filterReset = '[data-action="filterReset"]';
      $(document).off('click.filterReset.FPointer').on('click.filterReset.FPointer', filterReset, function () {
        filterApp.resetFilter($(this));
      });
    }
  };
  return filterApp;
});
define([
  'model',
  'data',
  'tool',
  'public',
  'viewTpl/rightModal/index'
], function (model, dataApp, tool, publicApp, rightModalTpl) {
  var app = {

    init: function (opt) {
      this.showRightModal(opt);
      this.events();
    },

    showRightModal: function (opt) {
      var $dom = $(".right-Modal");
      $dom.html(rightModalTpl(opt));
      if (opt.title) {
        $dom.find(".right-Modal-title").html(opt.title);
      }
      if (opt.body) {
        $dom.find(".right-Modal-body").html(opt.body);
      }
      $dom.fadeIn().find(".right-Modal-container").addClass("show");
    },

    events: function () {
      var closeRightModal = '[data-action="closeRightModal"]';
      $(document).off('click.closeRightModal.FPointer').on('click.closeRightModal.FPointer', closeRightModal, function () {
        $(".right-Modal").find(".right-Modal-container").removeClass("show");
        $(".right-Modal").fadeOut();
        $("table tr").removeClass("currentRow");
      });

      var emptyClose = '[data-action="empty-close"]';
      $(document).off('click.emptyClose.FPointer').on('click.emptyClose.FPointer', emptyClose, function () {
       $('[data-action="closeRightModal"]').click();
      });
    }
  };
  return app;
});
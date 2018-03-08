/*!
 * jQuery Textarea AutoSize plugin
 * Author: Javier Julio
 * Licensed under the MIT license
 */
;(function ($, window, document, undefined) {

  var pluginName = "textareaAutoSize";
  var pluginDataName = "plugin_" + pluginName;

  var containsText = function (value) {
    return (value.replace(/\s/g, '').length > 0);
  };

  function Plugin(element, options) {
    this.element = element;
    this.$element = $(element);
    this.init();
  }

  Plugin.prototype = {
    init: function() {
      var height = this.$element.outerHeight();
      var diff = parseInt(this.$element.css('paddingBottom')) +
                 parseInt(this.$element.css('paddingTop')) || 0;

      if (containsText(this.element.value)) {
        this.$element.height(this.element.scrollHeight - diff);
      }

      // keyup is required for IE to properly reset height when deleting text
      this.$element.on('input keyup change', function(event) {
        var $window = $(window);
        var currentScrollPosition = $window.scrollTop();
        
        $(this)
          .height(0)
          .height(this.scrollHeight - diff);
         // console.log($(this).height()+ "---------" + $(this).height());
          if($(this).height() < 30){
        	  $('.chat-wrapper').css('margin-bottom','0px' );
          }else if($(this).height() > 146){
        	  $('.chat-wrapper').css('margin-bottom','112px' );
          }else{
        	  $('.chat-wrapper').css('margin-bottom',($(this).height()-36) +'px' );
          }
         $window.scrollTop(currentScrollPosition);
       
      });
    }
  };

  $.fn[pluginName] = function (options) {
    this.each(function() {
      if (!$.data(this, pluginDataName)) {
        $.data(this, pluginDataName, new Plugin(this, options));
      }
    });
    return this;
  };

})(jQuery, window, document);
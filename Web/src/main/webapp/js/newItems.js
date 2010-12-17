		var items;
		var itemsToDisplayCount = 2;
		var lastUsedItems;

        Array.prototype.shuffle = function() {
            var s = [];
            while (this.length) s.push(this.splice(Math.random() * this.length, 1));
            while (s.length) this.push(s.pop());
            return this;
        };

		function main() {
            dojo.require("dojo.fx");
			items = dojo.query(".imageItem");
			lastUsedItems = items;

			shrinkDivs();
			prepareNewGoods();
			setInterval('prepareNewGoods()', 1500);
		}

		function shrinkDivs() {
			var divsToShrink = document.getElementsByTagName('div');
			for (i = 0; i < divsToShrink.length; i++) {
				var divToShrink = divsToShrink[i];
				var className = divToShrink.className;
				if (className == 'desc' || className == 'price') {
					if (divToShrink.innerHTML == '') {
						divToShrink.style.padding = '0';
					}
				}
			}
		}

        function prepareNewGoods() {
			var itemsToDisplay = [];
			if (items.length <= itemsToDisplayCount) {
				itemsToDisplay = items;
			} else {
                var indexesToUse = [];
                for (var i = 0; i < items.length; i++) {
                    indexesToUse[i] = i;
                }
                indexesToUse.shuffle();

                var splicedIndexesToUse = indexesToUse.slice(0, itemsToDisplayCount);
				for (var i = 0; i < itemsToDisplayCount; i++) {
					itemsToDisplay[i] = items[splicedIndexesToUse[i]];
				}
			}

			for(var i=0; i<lastUsedItems.length; i++) {
				var item = lastUsedItems[i];
				var animFadeOut = dojo.fadeOut({node: item ,duration: 500});
				animFadeOut.play();
                dojo.style(item, 'display', 'none');
            }

			for (var i = 0; i < itemsToDisplay.length; i++) {
				var item = itemsToDisplay[i];
				var animFadeIn = dojo.fadeIn({node: item ,duration: 500});
				animFadeIn.play();
                var animNoDisplay = dojo.style(item, 'display', 'block');
            }

			lastUsedItems = itemsToDisplay;
		}

		window.onload = main;
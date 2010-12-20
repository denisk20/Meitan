		var items;
		var itemsToDisplayCount = 3;
		var lastUsedItems;
        var currentAnimation;
        var mouseOver = false;

        var renewPeriod = 1000;
        var fadeOutTime = 500;
        var fadeInTime = 500;

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
			setInterval('prepareNewGoods()', renewPeriod);
		}

        //todo remove this
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
            if(mouseOver) {
                return;
            }
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
				currentAnimation = dojo.fadeOut({node: item ,duration: fadeOutTime});
				currentAnimation.play();
                item.style.display='none';
            }

			for (var i = 0; i < itemsToDisplay.length; i++) {
				var item = itemsToDisplay[i];
                item.style.display='block';
                currentAnimation = dojo.fadeIn({node: item ,duration: fadeInTime});
                currentAnimation.play();
            }

			lastUsedItems = itemsToDisplay;
		}

		window.onload = main;
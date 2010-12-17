		var items;
		var itemsToDisplayCount = 3;
		var lastUsedItems;

		function main() {
			items = dojo.query(".imageItem");
			lastUsedItems = items;

			shrinkDivs();
			prepareNewGoods();
			setInterval('prepareNewGoods()', 3000);
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

		function getUniqueIndex(usedIndexes, upperBound) {
			var index = Math.floor(Math.random() * (upperBound + 1));
			for (var i = 0; i < usedIndexes.length; i++) {
				if (usedIndexes[i] == index) {
					return getUniqueIndex(usedIndexes, upperBound);
				}
			}
			return index;
		}

		function prepareNewGoods() {
			var itemsToDisplay = [];
			if (items.length <= itemsToDisplayCount) {
				itemsToDisplay = items;
			} else {
				var usedIndexes = [];
				for (var i = 0; i < itemsToDisplayCount; i++) {
					var index = getUniqueIndex(usedIndexes, items.length);
					usedIndexes[i] = index;
				}

				for (var i = 0; i < itemsToDisplayCount; i++) {
					itemsToDisplay[i] = items[usedIndexes[i]];
				}
			}

			for(var i=0; i<lastUsedItems.length; i++) {
				var item = lastUsedItems[i];
//				var animFadeOut = dojo.fadeOut({node: item ,duration: 1000});
				var animFadeOut = dojo.fadeOut({node: item ,duration: 1000, onEnd: function() {
					item.style.display = 'none'
				}});

				animFadeOut.play();
			}

			for (var i = 0; i < itemsToDisplay.length; i++) {
				var item = itemsToDisplay[i];
//				var animFadeIn = dojo.fadeIn({node: item ,duration: 1000});
				var animFadeIn = dojo.fadeIn({node: item ,duration: 1000, beforeBegin: function() {
					item.style.display = 'block'
				}});

				animFadeIn.play();
			}

			lastUsedItems = itemsToDisplay;
		}

		window.onload = main;
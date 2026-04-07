var pm = {
    dateTimeOptions: {
        enableTime: true,
        enableSeconds: true,
        dateFormat: "Y-m-d H:i:S",
        time_24hr: true,

        onOpen: function(selectedDates, dateStr, instance) {
            if (selectedDates.length === 0) {
                const now = new Date();
                now.setHours(23, 59, 59, 0);
                instance.setDate(now, false);
            }
        }
    },

    initDateTimePicker: function(elementId) {
        flatpickr(elementId, pm.dateTimeOptions);
    },

    autocompleteOn: function(elementId, suggestionUri) {
        $(elementId).autocomplete({
            source: function(req, resp) {
                $.post(
                    suggestionUri,
                    { term: req.term },
                    function(data) {
                        resp(data);
                    },
                    'json'
                );
            },
            open: function() {
                $(this).autocomplete("widget").css({
                    "width": $(this).outerWidth() + "px"
                });
            }
        });
    }
};
var pm = {
    dateTimeOptions: {
        enableTime: true,
        enableSeconds: true,
        dateFormat: 'Y-m-d H:i:S',
        time_24hr: true,
        defaultHour: 23,
        defaultMinute: 59,
        defaultSecond: 59,
        onOpen: function(selectedDates, dateStr, instance) {
            if (selectedDates.length === 0) {
                instance.setDate(null, false);
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
            }
        });
    }
};
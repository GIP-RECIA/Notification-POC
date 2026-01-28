document.addEventListener('DOMContentLoaded', () => {
    const services = document.querySelectorAll('.service');
    services.forEach(service => {
        const enabledCheckbox = service.querySelector('.service-enabled');
        const overrideCheckbox = service.querySelector('.service-override');
        const overrideLabels = service.querySelectorAll('.label-override');
        const priorityCheckboxes = service.querySelectorAll('.priority-checkbox');
        const priorityLabels = service.querySelectorAll('.priority-label');
        function updatePriorityCheckboxes() {
            if (enabledCheckbox.checked) {
                overrideLabels.forEach(label => label.style.display = 'inline-block');
                priorityCheckboxes.forEach(cb => cb.parentElement.style.display = 'inline-block');
                priorityLabels.forEach(label => label.style.display = 'block');
            } else {
                overrideLabels.forEach(label => label.style.display = 'none');
                priorityCheckboxes.forEach(cb => cb.parentElement.style.display = 'none');
                priorityLabels.forEach(label => label.style.display = 'none');
            }
            if (overrideCheckbox.checked && enabledCheckbox.checked) {
                priorityCheckboxes.forEach(cb => cb.parentElement.style.display = 'inline-block');
                priorityLabels.forEach(label => label.style.display = 'block');
            } else {
                priorityCheckboxes.forEach(cb => cb.parentElement.style.display = 'none');
                priorityLabels.forEach(label => label.style.display = 'none');
            }
        }
        updatePriorityCheckboxes();
        enabledCheckbox.addEventListener('change', updatePriorityCheckboxes);
        overrideCheckbox.addEventListener('change', updatePriorityCheckboxes);
    });
});
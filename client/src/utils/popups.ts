export const showDeleteSuccessPopup = () => {
    // Show an alert or modal dialog to inform the user that the delete operation was successful
    alert('Item deleted successfully!');
};

export const showErrorPopup = (errorMessage: string) => {
    // Show an alert or modal dialog to inform the user about the error
    alert(errorMessage);
};
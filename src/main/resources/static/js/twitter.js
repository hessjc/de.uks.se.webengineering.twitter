/**
 * Created by Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de) on 28.06.2015.
 */

<!-- functionality to remove alerts -->
$().alert('close');

<!-- Script to activate tooltips -->
$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});

<!-- popover inclusion -->
$(function () {
    $('[data-toggle="popover"]').popover()
});
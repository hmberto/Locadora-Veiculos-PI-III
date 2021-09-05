var loading = document.getElementById("lding");

function hidePopup() {
  var popup = document.getElementById("popup");

  if(popup.classList.contains("hidepopup")) {
    popup.classList.remove("hidepopup");
  }
  else {
    popup.classList.add("hidepopup");
  }
}

function cancelarReserva() {
  popup.classList.add("hidepopup");
  loading.classList.remove("hideloading");

  var urlParams = new URLSearchParams(window.location.search);
  var cpf = atob(urlParams.get('u'));

  var url = "http://ec2-18-119-13-255.us-east-2.compute.amazonaws.com:8186/LocadoraVeiculos/location/delete";
  var json = '{"cpf": "' + cpf + '","idLocacao": "' + iDLocacao + '","idVeiculo": "' + idVeiculo + '"}';

  var xhttp = new XMLHttpRequest();
  xhttp.open("POST", url, true);
  xhttp.setRequestHeader("Content-Type", "application/json");
  xhttp.send(json);

  xhttp.addEventListener('loadend', () => {
    loading.classList.add("hideloading");
    if(xhttp.status == 200) {
      window.location.replace("/?q=px");
      window.localStorage.setItem("q", 'px');
    }
  });
}
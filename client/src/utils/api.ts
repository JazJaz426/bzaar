import { getLoginCookie } from "./cookie";
import { LatLong } from "../components/Mapbox";

const HOST = "http://localhost:3232";

async function queryAPI(
  endpoint: string,
  query_params: Record<string, string>
) {
  // query_params is a dictionary of key-value pairs that gets added to the URL as query parameters
  // e.g. { foo: "bar", hell: "o" } becomes "?foo=bar&hell=o"
  const paramsString = new URLSearchParams(query_params).toString();
  const url = `${HOST}/${endpoint}?${paramsString}`;
  const response = await fetch(url);
  if (!response.ok) {
    console.error(response.status, response.statusText);
  }
  return response.json();
}

// Note: all functions here/below access server api in same format as described above for different endpoints
export async function filtOver(keyword: String) {
  return await queryAPI("areaquery", {
    keyword: keyword.toString()
  });
  
}

export async function addPin(pin: LatLong) {
  return await queryAPI("add-pin", {
    uid: getLoginCookie() || "",
    lat: pin.lat.toString(),
    lon: pin.long.toString(),
  });
}

export async function removePin(pin: LatLong) {
  return await queryAPI("rmv-pin", {
    uid: getLoginCookie() || "",
    lat: pin.lat.toString(),
    lon: pin.long.toString(),
  });
}

export async function getPins() {
  return await queryAPI("list-pins", {
    uid: getLoginCookie() || "",
  });
}

export async function clearUser(uid: string = getLoginCookie() || "") {
  return await queryAPI("clear-user", {
    uid: uid,
  });
}

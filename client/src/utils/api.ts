import { getLoginId } from "./cookie";
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
  console.log('query api url is ', url);
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
    uid: getLoginId() || "",
    lat: pin.lat.toString(),
    lon: pin.long.toString(),
  });
}

export async function removePin(pin: LatLong) {
  return await queryAPI("rmv-pin", {
    uid: getLoginId() || "",
    lat: pin.lat.toString(),
    lon: pin.long.toString(),
  });
}

export async function getPins() {
  return await queryAPI("list-pins", {
    uid: getLoginId() || "",
  });
}

export async function clearUser(uid: string = getLoginId() || "") {
  return await queryAPI("clear-user", {
    uid: uid,
  });
}

// cookie uid is not the uid for current login user
// have to retrieve profile by email
export async function getUserProfile(email: string) {
  return await queryAPI("getUserProfile", {
    email: email,
  });
}

export async function getSellerProfile(userId: string) {
  return await queryAPI("getSellerProfile", {
    userId: userId,
  });
}

// queries for term project
export async function getAllItems() {
  return await queryAPI("getItems", {
  });
}

export async function getItemDetails(itemId: string) {
  return await queryAPI("getItems", {
    itemId: itemId,
  });
}

export async function getItemsByUser(userId: string) {
  return await queryAPI("getItems", {
    userId: userId,
  });
}
export async function claimItem(itemId: string) {
  return await queryAPI("claimItem", {
    itemId: itemId
  });
}


export async function logInteraction(userId: string, itemId: string, interactionType: string) {
  return await queryAPI("recordUserActivity", {
    userId: userId,
    itemId: itemId,
    interactionType: interactionType
  });
}


export async function getWatchList(userId: string) {
  return await queryAPI("getWatchList", {
    userId: userId,
  });
}

export async function modifyWatchList(userId: string, itemId: string, operation: string) {
  return await queryAPI("modifyWatchList", {
    userId: userId,
    itemId: itemId,
    operation: operation,
  });
}

export async function getClaimList(userId: string) {
  return await queryAPI("getClaimList", {
    userId: userId,
  });
}

export async function modifyClaimList(userId: string, itemId: string, operation: string) {
  return await queryAPI("modifyClaimList", {
    userId: userId,
    itemId: itemId,
    operation: operation,
  });
}

export async function searchItems(keyword: string) {
  return await queryAPI("searchItems", {
    keyword: keyword
  });
}

export async function postItem(formData: FormData) {
  const response =  await fetch(`${HOST}/postItem`, {
    method: "POST",
    body: formData,
  });
  console.log('response is ', response);
  if (!response.ok) {
    console.error(response.status, response.statusText);
  }
  return response.json();
}
export async function deleteItem(itemId: string,userId: string) {
  return await queryAPI("deleteItem(", {
    itemId: itemId,
    userId: userId
  });
};

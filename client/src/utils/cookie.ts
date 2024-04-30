import Cookies from "js-cookie";

// TODO: FIRESTORE PART 1:
// - Fill in these functions to add cookie functionality to the firebase login.

export function addLoginId(uid: string): void {
  Cookies.set('uid', uid);
}

export function addLoginEmail(email: string): void {
  Cookies.set('email', email);
}

export function removeLoginId(): void {
  Cookies.remove('uid');
}

export function removeLoginEmail(): void {
  Cookies.remove('email');
}

export function getLoginId(): string | undefined {
  return Cookies.get('uid');
}

export function getLoginEmail(): string {
  return Cookies.get('email') || '';
}

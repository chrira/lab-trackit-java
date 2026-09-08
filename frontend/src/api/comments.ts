import { client } from "./client";

// Mirrors ch.acend.trackit.domain.Comment field for field.
export interface Comment {
  id: number;
  taskId: number;
  author: string;
  body: string;
  createdAt: string;
}

export interface CreateCommentRequest {
  author: string;
  body: string;
}

export interface UpdateCommentRequest {
  body: string;
}

export async function listComments(taskId: number): Promise<Comment[]> {
  const response = await client.get<Comment[]>(`/tasks/${taskId}/comments`);
  return response.data;
}

export async function createComment(taskId: number, request: CreateCommentRequest): Promise<Comment> {
  const response = await client.post<Comment>(`/tasks/${taskId}/comments`, request);
  return response.data;
}

export async function updateComment(
  taskId: number,
  commentId: number,
  request: UpdateCommentRequest,
): Promise<Comment> {
  const response = await client.patch<Comment>(`/tasks/${taskId}/comments/${commentId}`, request);
  return response.data;
}

export async function deleteComment(taskId: number, commentId: number): Promise<void> {
  await client.delete(`/tasks/${taskId}/comments/${commentId}`);
}

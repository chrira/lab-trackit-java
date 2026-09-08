<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { listTasks, type Task } from "../api/tasks";
import {
  createComment,
  deleteComment,
  listComments,
  updateComment,
  type Comment,
} from "../api/comments";

const route = useRoute();
const taskId = computed(() => Number(route.params.id));

const task = ref<Task | null>(null);
const comments = ref<Comment[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);

const commentAuthor = ref("");
const commentBody = ref("");

const editingId = ref<number | null>(null);
const editingBody = ref("");

async function load() {
  loading.value = true;
  error.value = null;
  try {
    const tasks = await listTasks();
    task.value = tasks.find((t) => t.id === taskId.value) ?? null;
    comments.value = task.value ? await listComments(taskId.value) : [];
    if (!task.value) {
      error.value = "No task with this id.";
    }
  } catch {
    error.value = "Could not reach the backend.";
  } finally {
    loading.value = false;
  }
}

async function submitComment() {
  if (!commentAuthor.value.trim() || !commentBody.value.trim()) {
    error.value = "A comment needs an author and a body.";
    return;
  }
  error.value = null;
  try {
    await createComment(taskId.value, { author: commentAuthor.value, body: commentBody.value });
    commentAuthor.value = "";
    commentBody.value = "";
    comments.value = await listComments(taskId.value);
  } catch {
    error.value = "The comment could not be created.";
  }
}

function startEdit(comment: Comment) {
  editingId.value = comment.id;
  editingBody.value = comment.body;
}

function cancelEdit() {
  editingId.value = null;
  editingBody.value = "";
}

async function saveEdit(comment: Comment) {
  if (!editingBody.value.trim()) {
    error.value = "A comment body cannot be empty.";
    return;
  }
  error.value = null;
  try {
    await updateComment(taskId.value, comment.id, { body: editingBody.value });
    comments.value = await listComments(taskId.value);
    cancelEdit();
  } catch {
    error.value = "The comment could not be updated.";
  }
}

async function removeComment(comment: Comment) {
  error.value = null;
  try {
    await deleteComment(taskId.value, comment.id);
    comments.value = await listComments(taskId.value);
  } catch {
    error.value = "The comment could not be deleted.";
  }
}

onMounted(load);
</script>

<template>
  <section>
    <p><router-link to="/">&larr; Back to the board</router-link></p>

    <p v-if="loading">Loading task...</p>
    <p v-else-if="error && !task" class="error">{{ error }}</p>

    <template v-else-if="task">
      <h1>{{ task.title }}</h1>
      <p class="meta">
        <span class="status" :data-status="task.status">{{ task.status }}</span>
        <span class="project">{{ task.project }}</span>
      </p>

      <p v-if="error" class="error">{{ error }}</p>

      <h2>Comments</h2>
      <p v-if="comments.length === 0">No comments yet.</p>
      <ul v-else class="comment-list">
        <li v-for="comment in comments" :key="comment.id">
          <div class="comment-head">
            <span class="author">{{ comment.author }}</span>
            <span class="created-at">{{ comment.createdAt }}</span>
          </div>

          <template v-if="editingId === comment.id">
            <textarea v-model="editingBody" aria-label="Edit comment body"></textarea>
            <div class="comment-actions">
              <button type="button" @click="saveEdit(comment)">Save</button>
              <button type="button" @click="cancelEdit">Cancel</button>
            </div>
          </template>
          <template v-else>
            <p class="body">{{ comment.body }}</p>
            <div class="comment-actions">
              <button type="button" @click="startEdit(comment)">Edit</button>
              <button type="button" @click="removeComment(comment)">Delete</button>
            </div>
          </template>
        </li>
      </ul>

      <form class="new-comment" @submit.prevent="submitComment">
        <input v-model="commentAuthor" placeholder="Your name" aria-label="Comment author" />
        <textarea v-model="commentBody" placeholder="Add a comment" aria-label="Comment body"></textarea>
        <button type="submit">Add comment</button>
      </form>
    </template>
  </section>
</template>

<style scoped>
.meta {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}
.status {
  font-size: 0.7rem;
  letter-spacing: 0.06em;
  padding: 0.15rem 0.45rem;
  border-radius: 3px;
  background: #e6f2f3;
  color: #0f7c86;
}
.status[data-status="DONE"] {
  background: #e8efe6;
  color: #4a7c3f;
}
.project {
  color: #6b7a80;
  font-size: 0.85rem;
}
.comment-list {
  list-style: none;
  margin: 0 0 1.5rem;
  padding: 0;
}
.comment-list li {
  padding: 0.6rem 0.75rem;
  background: #ffffff;
  border: 1px solid #e1e8ea;
  border-radius: 4px;
  margin-bottom: 0.4rem;
}
.comment-head {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  color: #6b7a80;
  margin-bottom: 0.35rem;
}
.comment-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
}
.new-comment {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  max-width: 30rem;
}
.new-comment input,
.new-comment textarea,
textarea {
  padding: 0.5rem 0.75rem;
  border: 1px solid #c3ced2;
  border-radius: 4px;
  font: inherit;
}
button {
  padding: 0.4rem 0.9rem;
  border: 0;
  border-radius: 4px;
  background: #0f7c86;
  color: #ffffff;
  font: inherit;
  cursor: pointer;
  align-self: flex-start;
}
.error {
  color: #a3302b;
}
</style>

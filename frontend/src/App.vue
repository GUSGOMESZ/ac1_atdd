<template>
  <div class="app">
    <header>
      <h1>Forum Reward System</h1>
      <nav>
        <button :class="{ active: tab === 'alunos' }" @click="tab = 'alunos'">Alunos</button>
        <button :class="{ active: tab === 'ranking' }" @click="tab = 'ranking'">Ranking</button>
      </nav>
    </header>

    <main>
      <div v-if="tab === 'alunos'" class="tab-content">
        <AlunoForm @aluno-criado="carregarAlunos" />
        <ComentarioForm :alunos="alunos" @comentario-adicionado="carregarAlunos" />
        <AlunoList :alunos="alunos" @atualizar="carregarAlunos" />
      </div>

      <div v-if="tab === 'ranking'" class="tab-content">
        <ProcessRankingForm @resultado="mostrarResultado" />
        <RankingView v-if="resultado" :resultado="resultado" />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AlunoList from './components/AlunoList.vue'
import AlunoForm from './components/AlunoForm.vue'
import ComentarioForm from './components/ComentarioForm.vue'
import RankingView from './components/RankingView.vue'
import ProcessRankingForm from './components/ProcessRankingForm.vue'

const tab = ref('alunos')
const alunos = ref([])
const resultado = ref(null)

async function carregarAlunos() {
  const res = await fetch('/api/forum-rewards/alunos')
  alunos.value = await res.json()
}

function mostrarResultado(r) {
  resultado.value = r
}

onMounted(carregarAlunos)
</script>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: sans-serif;
  background: #f0f2f5;
  color: #333;
}

.app {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

header {
  background: #1976d2;
  color: white;
  padding: 16px 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

header h1 {
  font-size: 1.4rem;
}

nav {
  display: flex;
  gap: 8px;
}

nav button {
  background: transparent;
  color: white;
  border: 2px solid rgba(255,255,255,0.5);
  padding: 6px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

nav button.active,
nav button:hover {
  background: white;
  color: #1976d2;
  border-color: white;
}

.tab-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
}

.card h2 {
  font-size: 1.1rem;
  margin-bottom: 16px;
  color: #1976d2;
  border-bottom: 1px solid #e0e0e0;
  padding-bottom: 8px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
}

label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #555;
}

input, select {
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.2s;
}

input:focus, select:focus {
  border-color: #1976d2;
}

.btn {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 600;
  transition: opacity 0.2s;
}

.btn:hover {
  opacity: 0.85;
}

.btn-primary {
  background: #1976d2;
  color: white;
}

.btn-success {
  background: #2e7d32;
  color: white;
}

.btn-sm {
  padding: 4px 12px;
  font-size: 0.8rem;
}

.alert {
  padding: 10px 14px;
  border-radius: 4px;
  font-size: 0.85rem;
  margin-top: 8px;
}

.alert-success {
  background: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #a5d6a7;
}

.alert-error {
  background: #ffebee;
  color: #c62828;
  border: 1px solid #ef9a9a;
}
</style>
